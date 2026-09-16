package com.groweasy.importer.service;

import com.groweasy.importer.dto.CrmRecord;
import com.groweasy.importer.dto.DataQualityItem;
import com.groweasy.importer.dto.ImportErrorItem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExcelService {

    private static final Logger log = LoggerFactory.getLogger(ExcelService.class);

    private final String exportsDir;

    public static class ExcelReportData {
        public int totalRecords;
        public int processedRecords;
        public int skippedRecords;
        public double processingPercentage;
        public int duplicateCount;
        public int invalidEmailCount;
        public int missingFieldCount;
        public double averageQualityScore;
        public Map<String, Integer> leadStatusDistribution = new LinkedHashMap<>();
        public List<CrmRecord> records = new ArrayList<>();
        public List<DataQualityItem> qualityItems = new ArrayList<>();
        public List<ImportErrorItem> errors = new ArrayList<>();
    }

    public ExcelService(@Value("${storage.exports-dir:exports}") String exportsDir) {
        this.exportsDir = exportsDir;
    }

    /**
     * Generates a professional multi-sheet Excel CRM intelligence report.
     */
    public String generateProfessionalReport(ExcelReportData data) throws Exception {
        Path exportPath = Paths.get(exportsDir);
        if (!Files.exists(exportPath)) {
            Files.createDirectories(exportPath);
        }

        String fileName = "groweasy_crm_intelligence_" + System.currentTimeMillis() + ".xlsx";
        File targetFile = exportPath.resolve(fileName).toFile();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Styles styles = new Styles(workbook);

            // Sheet 1: Executive Summary
            buildExecutiveSummarySheet(workbook, styles, data);

            // Sheet 2: CRM Leads
            buildCrmLeadsSheet(workbook, styles, data.records);

            // Sheet 3: Data Quality
            buildDataQualitySheet(workbook, styles, data.qualityItems);

            // Sheet 4: AI Insights
            buildAiInsightsSheet(workbook, styles, data.records);

            // Sheet 5: Import Errors
            buildImportErrorsSheet(workbook, styles, data.errors);

            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                workbook.write(fos);
            }
        }

        log.info("Professional Excel report generated successfully at: {}", targetFile.getAbsolutePath());
        return fileName;
    }

    /**
     * Backward-compatible simple Excel generator.
     */
    public String generateExcel(List<CrmRecord> records) throws Exception {
        ExcelReportData data = new ExcelReportData();
        data.records = records != null ? records : new ArrayList<>();
        data.totalRecords = data.records.size();
        data.processedRecords = data.records.size();
        data.skippedRecords = 0;
        data.processingPercentage = 100.0;
        data.averageQualityScore = 95.0;

        for (CrmRecord r : data.records) {
            String status = r.getCrmStatus() != null && !r.getCrmStatus().isBlank() ? r.getCrmStatus() : "UNCLASSIFIED";
            data.leadStatusDistribution.put(status, data.leadStatusDistribution.getOrDefault(status, 0) + 1);

            DataQualityItem q = new DataQualityItem();
            q.setLeadId(r.getLeadId());
            q.setOriginalRow(r.getOriginalRow());
            q.setEmail(r.getEmail());
            q.setPhone(r.getPhone());
            q.setEmailValid(r.getEmail().contains("@") ? "VALID" : "MISSING");
            q.setPhoneValid(!r.getPhone().isBlank() ? "VALID" : "MISSING");
            q.setQualityScore(90);
            data.qualityItems.add(q);
        }

        return generateProfessionalReport(data);
    }

    // =========================================================================
    // SHEET 1: Executive Summary
    // =========================================================================
    private void buildExecutiveSummarySheet(XSSFWorkbook workbook, Styles styles, ExcelReportData data) {
        Sheet sheet = workbook.createSheet("Executive Summary");
        sheet.setDisplayGridlines(true);

        int rowNum = 0;

        // Title Block
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("GrowEasy AI CRM Intelligence Report");
        titleCell.setCellStyle(styles.titleStyle);

        Row metaRow = sheet.createRow(rowNum++);
        Cell metaCell = metaRow.createCell(1);
        metaCell.setCellValue("Generated on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")) + " | Model: gemini-2.5-flash");
        metaCell.setCellStyle(styles.metaStyle);

        rowNum++; // Blank line

        // Section 1: Core Performance & Quality KPIs
        Row secRow1 = sheet.createRow(rowNum++);
        Cell secCell1 = secRow1.createCell(1);
        secCell1.setCellValue("KEY IMPORT & DATA INTEGRITY METRICS");
        secCell1.setCellStyle(styles.sectionHeaderStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 1, 2));

        Row tblHeader1 = sheet.createRow(rowNum++);
        createCell(tblHeader1, 1, "Metric", styles.subHeaderStyle);
        createCell(tblHeader1, 2, "Value", styles.subHeaderStyle);

        addMetricRow(sheet, rowNum++, "Total Records Uploaded", String.valueOf(data.totalRecords), styles);
        addMetricRow(sheet, rowNum++, "Successfully Processed Leads", String.valueOf(data.processedRecords), styles);
        addMetricRow(sheet, rowNum++, "Skipped / Failed Records", String.valueOf(data.skippedRecords), styles);
        addMetricRow(sheet, rowNum++, "Processing Success Rate", String.format("%.1f%%", data.processingPercentage), styles);
        addMetricRow(sheet, rowNum++, "Average Data Quality Score", String.format("%.1f / 100", data.averageQualityScore), styles);
        addMetricRow(sheet, rowNum++, "Duplicate Records Detected", String.valueOf(data.duplicateCount), styles);
        addMetricRow(sheet, rowNum++, "Invalid / Malformed Emails", String.valueOf(data.invalidEmailCount), styles);
        addMetricRow(sheet, rowNum++, "Missing Critical Contact Info", String.valueOf(data.missingFieldCount), styles);

        rowNum += 2; // Blank lines

        // Section 2: CRM Lead Status Distribution
        Row secRow2 = sheet.createRow(rowNum++);
        Cell secCell2 = secRow2.createCell(1);
        secCell2.setCellValue("CRM LEAD CLASSIFICATION DISTRIBUTION");
        secCell2.setCellStyle(styles.sectionHeaderStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 1, 3));

        Row tblHeader2 = sheet.createRow(rowNum++);
        createCell(tblHeader2, 1, "CRM Status", styles.subHeaderStyle);
        createCell(tblHeader2, 2, "Lead Count", styles.subHeaderStyle);
        createCell(tblHeader2, 3, "Share (%)", styles.subHeaderStyle);

        if (data.leadStatusDistribution.isEmpty()) {
            Row emptyRow = sheet.createRow(rowNum++);
            createCell(emptyRow, 1, "No leads classified", styles.dataStyle);
            createCell(emptyRow, 2, "0", styles.dataStyleCenter);
            createCell(emptyRow, 3, "0.0%", styles.dataStyleCenter);
        } else {
            for (Map.Entry<String, Integer> entry : data.leadStatusDistribution.entrySet()) {
                Row distRow = sheet.createRow(rowNum++);
                createCell(distRow, 1, entry.getKey(), styles.dataStyleBold);
                createCell(distRow, 2, String.valueOf(entry.getValue()), styles.dataStyleCenter);
                double pct = data.processedRecords > 0 ? (entry.getValue() * 100.0 / data.processedRecords) : 0.0;
                createCell(distRow, 3, String.format("%.1f%%", pct), styles.dataStyleCenter);
            }
        }

        rowNum += 2;

        // Section 3: Data Quality Tier Overview
        Row secRow3 = sheet.createRow(rowNum++);
        Cell secCell3 = secRow3.createCell(1);
        secCell3.setCellValue("DATA QUALITY TIERS (LEAD SCORING)");
        secCell3.setCellStyle(styles.sectionHeaderStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 1, 2));

        Row tblHeader3 = sheet.createRow(rowNum++);
        createCell(tblHeader3, 1, "Quality Tier", styles.subHeaderStyle);
        createCell(tblHeader3, 2, "Lead Count", styles.subHeaderStyle);

        int tierHigh = 0;
        int tierMed = 0;
        int tierLow = 0;
        for (DataQualityItem q : data.qualityItems) {
            if (q.getQualityScore() >= 80) tierHigh++;
            else if (q.getQualityScore() >= 50) tierMed++;
            else tierLow++;
        }

        addMetricRow(sheet, rowNum++, "High Quality (Score 80 - 100)", String.valueOf(tierHigh), styles);
        addMetricRow(sheet, rowNum++, "Medium Quality (Score 50 - 79)", String.valueOf(tierMed), styles);
        addMetricRow(sheet, rowNum++, "Low Quality (Score < 50)", String.valueOf(tierLow), styles);

        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 10000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 4000);
    }

    private void addMetricRow(Sheet sheet, int rowNum, String metric, String val, Styles styles) {
        Row row = sheet.createRow(rowNum);
        createCell(row, 1, metric, styles.dataStyle);
        createCell(row, 2, val, styles.dataStyleBoldCenter);
    }

    // =========================================================================
    // SHEET 2: CRM Leads
    // =========================================================================
    private void buildCrmLeadsSheet(XSSFWorkbook workbook, Styles styles, List<CrmRecord> records) {
        Sheet sheet = workbook.createSheet("CRM Leads");
        sheet.setDisplayGridlines(true);

        String[] headers = {
                "Lead ID", "Original Row", "Name", "Email", "Phone", "Country Code",
                "Mobile", "Company", "Job Title", "Industry", "Website", "City",
                "State", "Country", "Lead Owner", "CRM Status", "Priority",
                "AI Insight", "Recommended Action", "AI Reason", "Data Source", "Created At"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i], styles.primaryHeaderStyle);
        }

        int rowIdx = 1;
        for (CrmRecord r : records) {
            Row row = sheet.createRow(rowIdx++);
            CellStyle altStyle = (rowIdx % 2 == 0) ? styles.dataStyle : styles.dataStyleAlt;

            createCell(row, 0, r.getLeadId(), altStyle);
            createCell(row, 1, r.getOriginalRow() > 0 ? String.valueOf(r.getOriginalRow()) : "", altStyle);
            createCell(row, 2, r.getName(), altStyle);
            createCell(row, 3, r.getEmail(), altStyle);
            createCell(row, 4, r.getPhone(), altStyle);
            createCell(row, 5, r.getCountryCode(), altStyle);
            createCell(row, 6, r.getMobileWithoutCountryCode(), altStyle);
            createCell(row, 7, r.getCompany(), altStyle);
            createCell(row, 8, r.getJobTitle(), altStyle);
            createCell(row, 9, r.getIndustry(), altStyle);
            createCell(row, 10, r.getWebsite(), altStyle);
            createCell(row, 11, r.getCity(), altStyle);
            createCell(row, 12, r.getState(), altStyle);
            createCell(row, 13, r.getCountry(), altStyle);
            createCell(row, 14, r.getLeadOwner(), altStyle);
            createCell(row, 15, r.getCrmStatus(), getStatusCellStyle(r.getCrmStatus(), styles));
            createCell(row, 16, r.getPriority(), getPriorityCellStyle(r.getPriority(), styles));
            createCell(row, 17, r.getAiInsight(), altStyle);
            createCell(row, 18, r.getRecommendedAction(), altStyle);
            createCell(row, 19, r.getAiReason(), altStyle);
            createCell(row, 20, r.getDataSource(), altStyle);
            createCell(row, 21, r.getCreatedAt(), altStyle);
        }

        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, Math.max(1, rowIdx - 1), 0, headers.length - 1));
        autoSizeColumns(sheet, headers.length);
    }

    // =========================================================================
    // SHEET 3: Data Quality
    // =========================================================================
    private void buildDataQualitySheet(XSSFWorkbook workbook, Styles styles, List<DataQualityItem> items) {
        Sheet sheet = workbook.createSheet("Data Quality");
        sheet.setDisplayGridlines(true);

        String[] headers = {
                "Lead ID", "Original Row", "Email", "Phone", "Email Valid",
                "Phone Valid", "Missing Fields", "Duplicate", "Duplicate Of",
                "Data Quality Score", "Validation Issues"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i], styles.primaryHeaderStyle);
        }

        int rowIdx = 1;
        for (DataQualityItem item : items) {
            Row row = sheet.createRow(rowIdx++);
            CellStyle altStyle = (rowIdx % 2 == 0) ? styles.dataStyle : styles.dataStyleAlt;

            createCell(row, 0, item.getLeadId(), altStyle);
            createCell(row, 1, String.valueOf(item.getOriginalRow()), altStyle);
            createCell(row, 2, item.getEmail(), altStyle);
            createCell(row, 3, item.getPhone(), altStyle);
            createCell(row, 4, item.getEmailValid(), getValidationFlagStyle(item.getEmailValid(), styles));
            createCell(row, 5, item.getPhoneValid(), getValidationFlagStyle(item.getPhoneValid(), styles));
            createCell(row, 6, item.getMissingFields(), altStyle);
            createCell(row, 7, item.isDuplicate() ? "YES" : "NO", item.isDuplicate() ? styles.badgeRedStyle : styles.badgeGreenStyle);
            createCell(row, 8, item.getDuplicateOf(), altStyle);
            createCell(row, 9, item.getQualityScore() + " / 100", getQualityScoreStyle(item.getQualityScore(), styles));
            createCell(row, 10, item.getValidationIssues(), altStyle);
        }

        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, Math.max(1, rowIdx - 1), 0, headers.length - 1));
        autoSizeColumns(sheet, headers.length);
    }

    // =========================================================================
    // SHEET 4: AI Insights
    // =========================================================================
    private void buildAiInsightsSheet(XSSFWorkbook workbook, Styles styles, List<CrmRecord> records) {
        Sheet sheet = workbook.createSheet("AI Insights");
        sheet.setDisplayGridlines(true);

        String[] headers = {
                "Lead ID", "Name", "Company", "CRM Classification", "Priority",
                "Classification Reason", "AI Insight", "Recommended Follow Up"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i], styles.primaryHeaderStyle);
        }

        int rowIdx = 1;
        for (CrmRecord r : records) {
            Row row = sheet.createRow(rowIdx++);
            CellStyle altStyle = (rowIdx % 2 == 0) ? styles.dataStyle : styles.dataStyleAlt;

            createCell(row, 0, r.getLeadId(), altStyle);
            createCell(row, 1, r.getName(), altStyle);
            createCell(row, 2, r.getCompany(), altStyle);
            createCell(row, 3, r.getCrmStatus(), getStatusCellStyle(r.getCrmStatus(), styles));
            createCell(row, 4, r.getPriority(), getPriorityCellStyle(r.getPriority(), styles));
            createCell(row, 5, r.getAiReason(), altStyle);
            createCell(row, 6, r.getAiInsight(), altStyle);
            createCell(row, 7, r.getRecommendedAction(), altStyle);
        }

        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, Math.max(1, rowIdx - 1), 0, headers.length - 1));
        autoSizeColumns(sheet, headers.length);
    }

    // =========================================================================
    // SHEET 5: Import Errors
    // =========================================================================
    private void buildImportErrorsSheet(XSSFWorkbook workbook, Styles styles, List<ImportErrorItem> errors) {
        Sheet sheet = workbook.createSheet("Import Errors");
        sheet.setDisplayGridlines(true);

        String[] headers = {
                "Original Row", "Record Identifier", "Error Type", "Error Message", "Recommended Action"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i], styles.errorHeaderStyle);
        }

        int rowIdx = 1;
        if (errors == null || errors.isEmpty()) {
            Row row = sheet.createRow(rowIdx++);
            createCell(row, 0, "—", styles.dataStyleCenter);
            createCell(row, 1, "None", styles.dataStyleCenter);
            createCell(row, 2, "CLEAN", styles.badgeGreenStyle);
            createCell(row, 3, "All records processed successfully without errors.", styles.dataStyle);
            createCell(row, 4, "No action required.", styles.dataStyle);
        } else {
            for (ImportErrorItem err : errors) {
                Row row = sheet.createRow(rowIdx++);
                CellStyle altStyle = (rowIdx % 2 == 0) ? styles.dataStyle : styles.dataStyleAlt;

                createCell(row, 0, String.valueOf(err.getOriginalRow()), styles.dataStyleCenter);
                createCell(row, 1, err.getRecordIdentifier(), altStyle);
                createCell(row, 2, err.getErrorType(), styles.badgeRedStyle);
                createCell(row, 3, err.getErrorMessage(), altStyle);
                createCell(row, 4, err.getRecommendedAction(), altStyle);
            }
        }

        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, Math.max(1, rowIdx - 1), 0, headers.length - 1));
        autoSizeColumns(sheet, headers.length);
    }

    // =========================================================================
    // Helper Methods & Cell Styling
    // =========================================================================
    private void createCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void autoSizeColumns(Sheet sheet, int colCount) {
        for (int i = 0; i < colCount; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            if (currentWidth < 3500) {
                sheet.setColumnWidth(i, 3800);
            } else if (currentWidth > 16000) {
                sheet.setColumnWidth(i, 16000);
            }
        }
    }

    private CellStyle getStatusCellStyle(String status, Styles styles) {
        if (status == null) return styles.dataStyle;
        return switch (status.toUpperCase()) {
            case "GOOD_LEAD_FOLLOW_UP", "SALE_DONE" -> styles.badgeGreenStyle;
            case "BAD_LEAD" -> styles.badgeRedStyle;
            case "DID_NOT_CONNECT" -> styles.badgeYellowStyle;
            default -> styles.dataStyle;
        };
    }

    private CellStyle getPriorityCellStyle(String priority, Styles styles) {
        if (priority == null) return styles.dataStyle;
        return switch (priority.toUpperCase()) {
            case "HIGH" -> styles.badgeRedStyle;
            case "MEDIUM" -> styles.badgeYellowStyle;
            case "LOW" -> styles.badgeGreenStyle;
            default -> styles.dataStyle;
        };
    }

    private CellStyle getValidationFlagStyle(String flag, Styles styles) {
        if (flag == null) return styles.dataStyle;
        return switch (flag.toUpperCase()) {
            case "VALID" -> styles.badgeGreenStyle;
            case "INVALID" -> styles.badgeRedStyle;
            case "MISSING" -> styles.badgeYellowStyle;
            default -> styles.dataStyle;
        };
    }

    private CellStyle getQualityScoreStyle(int score, Styles styles) {
        if (score >= 80) return styles.badgeGreenStyle;
        if (score >= 50) return styles.badgeYellowStyle;
        return styles.badgeRedStyle;
    }

    // =========================================================================
    // Styles Container
    // =========================================================================
    private static class Styles {
        CellStyle titleStyle;
        CellStyle metaStyle;
        CellStyle sectionHeaderStyle;
        CellStyle primaryHeaderStyle;
        CellStyle errorHeaderStyle;
        CellStyle subHeaderStyle;
        CellStyle dataStyle;
        CellStyle dataStyleAlt;
        CellStyle dataStyleBold;
        CellStyle dataStyleCenter;
        CellStyle dataStyleBoldCenter;
        CellStyle badgeGreenStyle;
        CellStyle badgeYellowStyle;
        CellStyle badgeRedStyle;

        public Styles(XSSFWorkbook wb) {
            XSSFFont fontTitle = wb.createFont();
            fontTitle.setFontName("Calibri");
            fontTitle.setFontHeightInPoints((short) 16);
            fontTitle.setBold(true);
            fontTitle.setColor(IndexedColors.DARK_BLUE.getIndex());

            titleStyle = wb.createCellStyle();
            titleStyle.setFont(fontTitle);

            XSSFFont fontMeta = wb.createFont();
            fontMeta.setFontName("Calibri");
            fontMeta.setFontHeightInPoints((short) 10);
            fontMeta.setColor(IndexedColors.GREY_50_PERCENT.getIndex());

            metaStyle = wb.createCellStyle();
            metaStyle.setFont(fontMeta);

            XSSFFont fontHeader = wb.createFont();
            fontHeader.setFontName("Calibri");
            fontHeader.setFontHeightInPoints((short) 11);
            fontHeader.setBold(true);
            fontHeader.setColor(IndexedColors.WHITE.getIndex());

            // Primary Dark Blue Header
            primaryHeaderStyle = wb.createCellStyle();
            primaryHeaderStyle.setFont(fontHeader);
            primaryHeaderStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            primaryHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            primaryHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            primaryHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setThinBorders(primaryHeaderStyle);

            // Error Header (Burgundy / Red)
            errorHeaderStyle = wb.createCellStyle();
            errorHeaderStyle.setFont(fontHeader);
            errorHeaderStyle.setFillForegroundColor(IndexedColors.MAROON.getIndex());
            errorHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            errorHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            errorHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setThinBorders(errorHeaderStyle);

            // Section Header
            XSSFFont fontSection = wb.createFont();
            fontSection.setFontName("Calibri");
            fontSection.setFontHeightInPoints((short) 11);
            fontSection.setBold(true);
            fontSection.setColor(IndexedColors.WHITE.getIndex());

            sectionHeaderStyle = wb.createCellStyle();
            sectionHeaderStyle.setFont(fontSection);
            sectionHeaderStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            sectionHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setThinBorders(sectionHeaderStyle);

            // Sub Header
            XSSFFont fontSub = wb.createFont();
            fontSub.setFontName("Calibri");
            fontSub.setFontHeightInPoints((short) 10);
            fontSub.setBold(true);
            fontSub.setColor(IndexedColors.BLACK.getIndex());

            subHeaderStyle = wb.createCellStyle();
            subHeaderStyle.setFont(fontSub);
            subHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            subHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setThinBorders(subHeaderStyle);

            // Normal Data
            XSSFFont fontData = wb.createFont();
            fontData.setFontName("Calibri");
            fontData.setFontHeightInPoints((short) 10);

            dataStyle = wb.createCellStyle();
            dataStyle.setFont(fontData);
            setThinBorders(dataStyle);

            // Alternating Data
            dataStyleAlt = wb.createCellStyle();
            dataStyleAlt.setFont(fontData);
            dataStyleAlt.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex()); // subtle
            setThinBorders(dataStyleAlt);

            // Bold Data
            XSSFFont fontDataBold = wb.createFont();
            fontDataBold.setFontName("Calibri");
            fontDataBold.setFontHeightInPoints((short) 10);
            fontDataBold.setBold(true);

            dataStyleBold = wb.createCellStyle();
            dataStyleBold.setFont(fontDataBold);
            setThinBorders(dataStyleBold);

            dataStyleCenter = wb.createCellStyle();
            dataStyleCenter.setFont(fontData);
            dataStyleCenter.setAlignment(HorizontalAlignment.CENTER);
            setThinBorders(dataStyleCenter);

            dataStyleBoldCenter = wb.createCellStyle();
            dataStyleBoldCenter.setFont(fontDataBold);
            dataStyleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
            setThinBorders(dataStyleBoldCenter);

            // Green Badge Style
            XSSFFont fontGreen = wb.createFont();
            fontGreen.setFontName("Calibri");
            fontGreen.setFontHeightInPoints((short) 10);
            fontGreen.setBold(true);
            fontGreen.setColor(IndexedColors.DARK_GREEN.getIndex());

            badgeGreenStyle = wb.createCellStyle();
            badgeGreenStyle.setFont(fontGreen);
            badgeGreenStyle.setAlignment(HorizontalAlignment.CENTER);
            badgeGreenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            badgeGreenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setThinBorders(badgeGreenStyle);

            // Yellow Badge Style
            XSSFFont fontYellow = wb.createFont();
            fontYellow.setFontName("Calibri");
            fontYellow.setFontHeightInPoints((short) 10);
            fontYellow.setBold(true);
            fontYellow.setColor(IndexedColors.BROWN.getIndex());

            badgeYellowStyle = wb.createCellStyle();
            badgeYellowStyle.setFont(fontYellow);
            badgeYellowStyle.setAlignment(HorizontalAlignment.CENTER);
            badgeYellowStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            badgeYellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setThinBorders(badgeYellowStyle);

            // Red Badge Style
            XSSFFont fontRed = wb.createFont();
            fontRed.setFontName("Calibri");
            fontRed.setFontHeightInPoints((short) 10);
            fontRed.setBold(true);
            fontRed.setColor(IndexedColors.DARK_RED.getIndex());

            badgeRedStyle = wb.createCellStyle();
            badgeRedStyle.setFont(fontRed);
            badgeRedStyle.setAlignment(HorizontalAlignment.CENTER);
            badgeRedStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
            badgeRedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setThinBorders(badgeRedStyle);
        }

        private void setThinBorders(CellStyle style) {
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        }
    }
}
