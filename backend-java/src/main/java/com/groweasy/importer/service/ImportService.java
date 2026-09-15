package com.groweasy.importer.service;

import com.groweasy.importer.dto.AiExtractionResponse;
import com.groweasy.importer.dto.ImportResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    private final CsvService csvService;
    private final GeminiService geminiService;
    private final ExcelService excelService;

    public ImportService(
            CsvService csvService,
            GeminiService geminiService,
            ExcelService excelService) {
        this.csvService = csvService;
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

        log.info("Starting CSV processing for: {} (size: {} bytes)", file.getOriginalFilename(), file.getSize());

        // 1. Parse CSV rows
        List<Map<String, Object>> rows = csvService.parseCsv(file);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("The uploaded CSV file contains no data rows.");
        }

        log.info("CSV parsed successfully. Total rows: {}", rows.size());

        // 2. AI Extraction and Mapping via Gemini
        AiExtractionResponse extractionResult = geminiService.processCsvRecords(rows);

        // 3. Generate Excel file
        String excelFileName = excelService.generateExcel(extractionResult.getRecords());

        // 4. Construct response
        String downloadUrl = "/exports/" + excelFileName;
        int totalImported = extractionResult.getRecords().size();
        int totalSkipped = extractionResult.getSkipped();

        log.info("Import completed: {} imported, {} skipped. Download at: {}",
                totalImported, totalSkipped, downloadUrl);

        return ImportResponse.success(
                totalImported,
                totalSkipped,
                extractionResult.getRecords(),
                downloadUrl
        );
    }
}
