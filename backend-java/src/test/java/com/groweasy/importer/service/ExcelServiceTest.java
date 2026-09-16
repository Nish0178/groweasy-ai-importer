package com.groweasy.importer.service;

import com.groweasy.importer.dto.CrmRecord;
import com.groweasy.importer.dto.DataQualityItem;
import com.groweasy.importer.dto.ImportErrorItem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExcelServiceTest {

    @Test
    @DisplayName("ExcelService: Should create professional 5-sheet workbook with correct sheet names and data")
    void testMultiSheetGeneration(@TempDir Path tempDir) throws Exception {
        ExcelService excelService = new ExcelService(tempDir.toString());

        ExcelService.ExcelReportData data = new ExcelService.ExcelReportData();
        data.totalRecords = 10;
        data.processedRecords = 8;
        data.skippedRecords = 2;
        data.processingPercentage = 80.0;
        data.duplicateCount = 1;
        data.invalidEmailCount = 1;
        data.missingFieldCount = 2;
        data.averageQualityScore = 87.5;
        data.leadStatusDistribution = Map.of("GOOD_LEAD_FOLLOW_UP", 5, "NEEDS_MORE_INFO", 3);

        CrmRecord lead = new CrmRecord();
        lead.setLeadId("LEAD-0001");
        lead.setOriginalRow(2);
        lead.setName("Test User");
        lead.setEmail("test@groweasy.com");
        lead.setPhone("9876543210");
        lead.setCompany("Acme Corp");
        lead.setCrmStatus("GOOD_LEAD_FOLLOW_UP");
        lead.setPriority("HIGH");
        lead.setAiInsight("Strong buying signal");
        lead.setRecommendedAction("Call immediately");
        lead.setConfidence("HIGH");
        data.records = List.of(lead);

        DataQualityItem qItem = new DataQualityItem();
        qItem.setLeadId("LEAD-0001");
        qItem.setOriginalRow(2);
        qItem.setEmail("test@groweasy.com");
        qItem.setPhone("9876543210");
        qItem.setEmailValid("VALID");
        qItem.setPhoneValid("VALID");
        qItem.setQualityScore(95);
        data.qualityItems = List.of(qItem);

        ImportErrorItem errorItem = new ImportErrorItem(
                3, "Ghost Lead", "Missing Contact", "No email or phone", "Provide contact"
        );
        data.errors = List.of(errorItem);

        String fileName = excelService.generateProfessionalReport(data);
        assertNotNull(fileName);

        File generatedFile = tempDir.resolve(fileName).toFile();
        assertTrue(generatedFile.exists(), "Generated Excel file must exist");
        assertTrue(generatedFile.length() > 0, "Excel file must not be empty");

        // Verify workbook sheets
        try (FileInputStream fis = new FileInputStream(generatedFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            assertEquals(5, workbook.getNumberOfSheets(), "Workbook must contain exactly 5 sheets");

            Sheet sheet1 = workbook.getSheet("Executive Summary");
            assertNotNull(sheet1, "Sheet 1 'Executive Summary' must exist");

            Sheet sheet2 = workbook.getSheet("CRM Leads");
            assertNotNull(sheet2, "Sheet 2 'CRM Leads' must exist");
            assertEquals("Lead ID", sheet2.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Test User", sheet2.getRow(1).getCell(2).getStringCellValue());

            Sheet sheet3 = workbook.getSheet("Data Quality");
            assertNotNull(sheet3, "Sheet 3 'Data Quality' must exist");
            assertEquals("Lead ID", sheet3.getRow(0).getCell(0).getStringCellValue());

            Sheet sheet4 = workbook.getSheet("AI Insights");
            assertNotNull(sheet4, "Sheet 4 'AI Insights' must exist");
            assertEquals("Lead ID", sheet4.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Confidence", sheet4.getRow(0).getCell(7).getStringCellValue());
            assertEquals("HIGH", sheet4.getRow(1).getCell(7).getStringCellValue());

            Sheet sheet5 = workbook.getSheet("Import Errors");
            assertNotNull(sheet5, "Sheet 5 'Import Errors' must exist");
            assertEquals("Original Row", sheet5.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Ghost Lead", sheet5.getRow(1).getCell(1).getStringCellValue());
        }
    }
}
