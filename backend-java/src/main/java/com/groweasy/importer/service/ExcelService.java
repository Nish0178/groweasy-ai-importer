package com.groweasy.importer.service;

import com.groweasy.importer.dto.CrmRecord;
import org.apache.poi.ss.usermodel.*;
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
import java.util.List;

@Service
public class ExcelService {

    private static final Logger log = LoggerFactory.getLogger(ExcelService.class);

    private final String exportsDir;

    private static final String[] HEADERS = {
            "created_at",
            "name",
            "email",
            "country_code",
            "mobile_without_country_code",
            "company",
            "city",
            "state",
            "country",
            "lead_owner",
            "crm_status",
            "crm_note",
            "data_source",
            "possession_time",
            "description"
    };

    public ExcelService(@Value("${storage.exports-dir:exports}") String exportsDir) {
        this.exportsDir = exportsDir;
    }

    /**
     * Generates an Excel (.xlsx) file for the CRM records and returns the generated filename.
     */
    public String generateExcel(List<CrmRecord> records) throws Exception {
        Path exportPath = Paths.get(exportsDir);
        if (!Files.exists(exportPath)) {
            Files.createDirectories(exportPath);
        }

        String fileName = "crm_records_" + System.currentTimeMillis() + ".xlsx";
        File targetFile = exportPath.resolve(fileName).toFile();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("CRM Records");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Calibri");
            font.setFontHeightInPoints((short) 11);
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Data Cell Style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create Data Rows
            int rowIdx = 1;
            for (CrmRecord record : records) {
                Row row = sheet.createRow(rowIdx++);

                createCell(row, 0, record.getCreatedAt(), dataStyle);
                createCell(row, 1, record.getName(), dataStyle);
                createCell(row, 2, record.getEmail(), dataStyle);
                createCell(row, 3, record.getCountryCode(), dataStyle);
                createCell(row, 4, record.getMobileWithoutCountryCode(), dataStyle);
                createCell(row, 5, record.getCompany(), dataStyle);
                createCell(row, 6, record.getCity(), dataStyle);
                createCell(row, 7, record.getState(), dataStyle);
                createCell(row, 8, record.getCountry(), dataStyle);
                createCell(row, 9, record.getLeadOwner(), dataStyle);
                createCell(row, 10, record.getCrmStatus(), dataStyle);
                createCell(row, 11, record.getCrmNote(), dataStyle);
                createCell(row, 12, record.getDataSource(), dataStyle);
                createCell(row, 13, record.getPossessionTime(), dataStyle);
                createCell(row, 14, record.getDescription(), dataStyle);
            }

            // Auto-size columns (capped at reasonable widths)
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                if (currentWidth < 3000) {
                    sheet.setColumnWidth(i, 3500);
                } else if (currentWidth > 15000) {
                    sheet.setColumnWidth(i, 15000);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                workbook.write(fos);
            }
        }

        log.info("Excel report generated successfully at: {}", targetFile.getAbsolutePath());
        return fileName;
    }

    private void createCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }
}
