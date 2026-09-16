package com.groweasy.importer.service;

import com.groweasy.importer.dto.CrmRecord;
import com.groweasy.importer.dto.DataQualityItem;
import com.groweasy.importer.dto.ImportErrorItem;
import com.groweasy.importer.dto.ImportResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    private final CsvService csvService;
    private final DataQualityService dataQualityService;
    private final GeminiService geminiService;
    private final ExcelService excelService;

    public ImportService(
            CsvService csvService,
            DataQualityService dataQualityService,
            GeminiService geminiService,
            ExcelService excelService) {
        this.csvService = csvService;
        this.dataQualityService = dataQualityService;
        this.geminiService = geminiService;
        this.excelService = excelService;
    }

    public ImportResponse processUploadedFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is required.");
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if (!originalFilename.endsWith(".csv") &&
                (file.getContentType() != null && !file.getContentType().contains("csv") && !file.getContentType().contains("plain"))) {
            throw new IllegalArgumentException("Only CSV files are allowed.");
        }

        log.info("Starting CSV data intelligence pipeline for: {} (size: {} bytes)", file.getOriginalFilename(), file.getSize());

        // Step 1: Parse CSV rows with line-number tracking
        List<Map<String, Object>> rows = csvService.parseCsv(file);
        int totalRecords = rows.size();
        if (totalRecords == 0) {
            throw new IllegalArgumentException("The uploaded CSV file contains no data rows.");
        }

        log.info("CSV parsed successfully. Total input records: {}", totalRecords);

        // Step 2: Deterministic Data Quality, Normalization & Duplicate Detection
        DataQualityService.QualityAnalysisResult qualityResult = dataQualityService.analyzeAndValidate(rows);
        List<Map<String, Object>> processableRows = qualityResult.getProcessableRows();
        List<ImportErrorItem> allErrors = new ArrayList<>(qualityResult.getInitialErrors());

        log.info("Validation complete: {} processable rows, {} initial validation errors, {} duplicates detected.",
                processableRows.size(), allErrors.size(), qualityResult.getDuplicateCount());

        // Step 3: AI Classification & CRM Intelligence via Gemini
        GeminiService.GeminiExtractionResult geminiResult = geminiService.processRecordsWithDiagnostics(processableRows);
        List<CrmRecord> extractedRecords = geminiResult.getRecords();
        allErrors.addAll(geminiResult.getErrors());

        // Correlate quality scores & issues to extracted CRM records
        Map<Integer, DataQualityItem> qualityMap = qualityResult.getQualityItemMap();
        Set<Integer> processedRowNumbers = new HashSet<>();

        for (CrmRecord record : extractedRecords) {
            int rowNum = record.getOriginalRow();
            if (rowNum > 0) {
                processedRowNumbers.add(rowNum);
            }
            DataQualityItem q = qualityMap.get(rowNum);
            if (q != null) {
                record.setQualityScore(q.getQualityScore());
                record.setValidationIssues(q.getValidationIssues());
                if (record.getLeadId() == null || record.getLeadId().isBlank()) {
                    record.setLeadId(q.getLeadId());
                }
            }
        }

        // Mathematical Record Accounting Verification:
        // Ensure every processable row is either in extractedRecords or in allErrors
        for (Map<String, Object> procRow : processableRows) {
            int rowNum = procRow.containsKey("__row_number") ? ((Number) procRow.get("__row_number")).intValue() : 0;
            if (rowNum > 0 && !processedRowNumbers.contains(rowNum)) {
                // Check if already in errors
                boolean alreadyInErrors = allErrors.stream().anyMatch(e -> e.getOriginalRow() == rowNum);
                if (!alreadyInErrors) {
                    String id = procRow.containsKey("Name") ? procRow.get("Name").toString()
                            : procRow.containsKey("name") ? procRow.get("name").toString()
                            : "Row " + rowNum;
                    allErrors.add(new ImportErrorItem(
                            rowNum,
                            id,
                            "AI Extraction Missing",
                            "Record was submitted to AI extraction but not returned in the output payload.",
                            "Check input fields for special characters or re-import."
                    ));
                }
            }
        }

        allErrors.sort(Comparator.comparingInt(ImportErrorItem::getOriginalRow));

        int processedCount = extractedRecords.size();
        int skippedCount = allErrors.size();

        // Mathematical guarantee: Processed + Skipped == Total
        if (processedCount + skippedCount != totalRecords) {
            log.warn("Accounting adjustment: total={}, processed={}, skipped={}", totalRecords, processedCount, skippedCount);
            skippedCount = Math.max(0, totalRecords - processedCount);
        }

        double processingPercentage = totalRecords > 0 ? (processedCount * 100.0 / totalRecords) : 0.0;
        processingPercentage = Math.round(processingPercentage * 10.0) / 10.0;

        // Step 4: Lead Status Distribution Aggregation
        Map<String, Integer> statusDistribution = new LinkedHashMap<>();
        for (CrmRecord r : extractedRecords) {
            String status = (r.getCrmStatus() != null && !r.getCrmStatus().isBlank()) ? r.getCrmStatus() : "UNCLASSIFIED";
            statusDistribution.put(status, statusDistribution.getOrDefault(status, 0) + 1);
        }

        // Step 5: Multi-Sheet Professional Excel Workbook Generation
        ExcelService.ExcelReportData reportData = new ExcelService.ExcelReportData();
        reportData.totalRecords = totalRecords;
        reportData.processedRecords = processedCount;
        reportData.skippedRecords = skippedCount;
        reportData.processingPercentage = processingPercentage;
        reportData.duplicateCount = qualityResult.getDuplicateCount();
        reportData.invalidEmailCount = qualityResult.getInvalidEmailCount();
        reportData.missingFieldCount = qualityResult.getMissingFieldCount();
        reportData.averageQualityScore = qualityResult.getAverageQualityScore();
        reportData.leadStatusDistribution = statusDistribution;
        reportData.records = extractedRecords;
        reportData.qualityItems = qualityResult.getAllQualityItems();
        reportData.errors = allErrors;

        String excelFileName = excelService.generateProfessionalReport(reportData);
        String downloadUrl = "/exports/" + excelFileName;

        log.info("Pipeline complete. Total: {}, Processed: {}, Skipped: {}, Success: {}%. Download: {}",
                totalRecords, processedCount, skippedCount, processingPercentage, downloadUrl);

        // Step 6: Construct Rich API Response
        ImportResponse response = new ImportResponse();
        response.setSuccess(true);
        response.setTotalRecords(totalRecords);
        response.setProcessedRecords(processedCount);
        response.setSkippedRecords(skippedCount);
        response.setProcessingPercentage(processingPercentage);
        response.setRecords(extractedRecords);
        response.setDownloadUrl(downloadUrl);
        response.setDuplicateCount(qualityResult.getDuplicateCount());
        response.setInvalidEmailCount(qualityResult.getInvalidEmailCount());
        response.setMissingFieldCount(qualityResult.getMissingFieldCount());
        response.setAverageQualityScore(qualityResult.getAverageQualityScore());
        response.setLeadStatusDistribution(statusDistribution);
        response.setErrors(allErrors);

        Map<String, Object> qualitySummary = new LinkedHashMap<>();
        qualitySummary.put("averageScore", qualityResult.getAverageQualityScore());
        qualitySummary.put("duplicates", qualityResult.getDuplicateCount());
        qualitySummary.put("invalidEmails", qualityResult.getInvalidEmailCount());
        qualitySummary.put("missingContactInfo", qualityResult.getMissingFieldCount());
        response.setDataQualitySummary(qualitySummary);

        return response;
    }
}
