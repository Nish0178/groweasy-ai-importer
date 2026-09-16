package com.groweasy.importer.service;

import com.groweasy.importer.dto.CrmRecord;
import com.groweasy.importer.dto.ImportErrorItem;
import com.groweasy.importer.dto.ImportResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class RecordAccountingTest {

    @Test
    @DisplayName("Record Accounting: Total == Processed + Skipped with mixed success and unprocessable rows")
    void testRecordAccountingGuarantee() throws Exception {
        CsvService csvService = mock(CsvService.class);
        DataQualityService dataQualityService = new DataQualityService();
        GeminiService geminiService = mock(GeminiService.class);
        ExcelService excelService = mock(ExcelService.class);

        when(excelService.generateProfessionalReport(any())).thenReturn("report.xlsx");

        // 5 input rows:
        // 3 valid, 2 without any contact info (unprocessable)
        List<Map<String, Object>> mockRows = List.of(
                Map.of("__row_number", 2, "name", "Lead 1", "email", "l1@test.com", "phone", "9876543210"),
                Map.of("__row_number", 3, "name", "Lead 2 (Ghost)", "email", "", "phone", ""),
                Map.of("__row_number", 4, "name", "Lead 3", "email", "l3@test.com", "phone", "9876543212"),
                Map.of("__row_number", 5, "name", "Lead 4 (Ghost)", "email", "", "phone", ""),
                Map.of("__row_number", 6, "name", "Lead 5", "email", "l5@test.com", "phone", "9876543214")
        );

        when(csvService.parseCsv(any())).thenReturn(mockRows);

        // Gemini successfully extracts the 3 processable rows
        CrmRecord r1 = new CrmRecord(); r1.setOriginalRow(2); r1.setName("Lead 1"); r1.setCrmStatus("GOOD_LEAD_FOLLOW_UP");
        CrmRecord r3 = new CrmRecord(); r3.setOriginalRow(4); r3.setName("Lead 3"); r3.setCrmStatus("NEEDS_MORE_INFO");
        CrmRecord r5 = new CrmRecord(); r5.setOriginalRow(6); r5.setName("Lead 5"); r5.setCrmStatus("SALE_DONE");

        when(geminiService.processRecordsWithDiagnostics(anyList())).thenReturn(
                new GeminiService.GeminiExtractionResult(List.of(r1, r3, r5), Collections.emptyList())
        );

        ImportService importService = new ImportService(csvService, dataQualityService, geminiService, excelService);

        MockMultipartFile file = new MockMultipartFile(
                "file", "leads.csv", "text/csv", "name,email,phone\n...".getBytes(StandardCharsets.UTF_8)
        );

        ImportResponse response = importService.processUploadedFile(file);

        assertNotNull(response);
        assertEquals(5, response.getTotalRecords(), "Total records must be 5");
        assertEquals(3, response.getProcessedRecords(), "Processed records must be 3");
        assertEquals(2, response.getSkippedRecords(), "Skipped records must be 2");
        assertEquals(5, response.getProcessedRecords() + response.getSkippedRecords(),
                "Mathematical accounting invariant: Processed + Skipped == Total");
        assertEquals(60.0, response.getProcessingPercentage(), 0.01);
    }

    @Test
    @DisplayName("Record Accounting: When Gemini batch fails, failed rows are accounted in skipped and errors")
    void testBatchFailureAccounting() throws Exception {
        CsvService csvService = mock(CsvService.class);
        DataQualityService dataQualityService = new DataQualityService();
        GeminiService geminiService = mock(GeminiService.class);
        ExcelService excelService = mock(ExcelService.class);

        when(excelService.generateProfessionalReport(any())).thenReturn("report.xlsx");

        List<Map<String, Object>> mockRows = List.of(
                Map.of("__row_number", 2, "name", "Lead A", "email", "a@test.com", "phone", "9876543210"),
                Map.of("__row_number", 3, "name", "Lead B", "email", "b@test.com", "phone", "9876543211")
        );

        when(csvService.parseCsv(any())).thenReturn(mockRows);

        // Simulate Gemini failure for Lead B
        CrmRecord rA = new CrmRecord(); rA.setOriginalRow(2); rA.setName("Lead A");
        ImportErrorItem errB = new ImportErrorItem(3, "Lead B", "AI Processing Failure", "Timeout", "Retry");

        when(geminiService.processRecordsWithDiagnostics(anyList())).thenReturn(
                new GeminiService.GeminiExtractionResult(List.of(rA), List.of(errB))
        );

        ImportService importService = new ImportService(csvService, dataQualityService, geminiService, excelService);

        MockMultipartFile file = new MockMultipartFile(
                "file", "leads.csv", "text/csv", "name,email,phone\n...".getBytes(StandardCharsets.UTF_8)
        );

        ImportResponse response = importService.processUploadedFile(file);

        assertEquals(2, response.getTotalRecords());
        assertEquals(1, response.getProcessedRecords());
        assertEquals(1, response.getSkippedRecords());
        assertEquals(2, response.getProcessedRecords() + response.getSkippedRecords());
        assertEquals(1, response.getErrors().size());
        assertEquals("Lead B", response.getErrors().get(0).getRecordIdentifier());
    }
}
