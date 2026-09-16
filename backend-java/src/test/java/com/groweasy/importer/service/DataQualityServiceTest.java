package com.groweasy.importer.service;

import com.groweasy.importer.dto.DataQualityItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DataQualityServiceTest {

    private DataQualityService dataQualityService;

    @BeforeEach
    void setUp() {
        dataQualityService = new DataQualityService();
    }

    @Test
    @DisplayName("Should detect valid, invalid, and missing emails accurately")
    void testEmailValidation() {
        List<Map<String, Object>> rows = List.of(
                Map.of("__row_number", 2, "name", "John Doe", "email", "john.doe@example.com", "phone", "9876543210"),
                Map.of("__row_number", 3, "name", "Bad Email", "email", "invalid-email-address", "phone", "9876543211"),
                Map.of("__row_number", 4, "name", "No Email", "email", "", "phone", "9876543212")
        );

        DataQualityService.QualityAnalysisResult result = dataQualityService.analyzeAndValidate(rows);

        assertEquals(1, result.getInvalidEmailCount());
        Map<Integer, DataQualityItem> map = result.getQualityItemMap();

        assertEquals("VALID", map.get(2).getEmailValid());
        assertEquals("INVALID", map.get(3).getEmailValid());
        assertEquals("MISSING", map.get(4).getEmailValid());
    }

    @Test
    @DisplayName("Should detect duplicates by email and by phone")
    void testDuplicateDetection() {
        List<Map<String, Object>> rows = List.of(
                Map.of("__row_number", 2, "name", "Alice", "email", "alice@domain.com", "phone", "9998887770"),
                Map.of("__row_number", 3, "name", "Alice Duplicate", "email", "alice@domain.com", "phone", "1234567890"),
                Map.of("__row_number", 4, "name", "Bob Phone Duplicate", "email", "bob@domain.com", "phone", "+91 9998887770")
        );

        DataQualityService.QualityAnalysisResult result = dataQualityService.analyzeAndValidate(rows);

        assertTrue(result.getDuplicateCount() >= 2, "Should detect at least 2 duplicates");
        Map<Integer, DataQualityItem> map = result.getQualityItemMap();

        assertFalse(map.get(2).isDuplicate());
        assertTrue(map.get(3).isDuplicate());
        assertTrue(map.get(4).isDuplicate());
    }

    @Test
    @DisplayName("Should mark rows missing both email and phone as unprocessable")
    void testUnprocessableRows() {
        List<Map<String, Object>> rows = List.of(
                Map.of("__row_number", 2, "name", "Valid Lead", "email", "valid@test.com", "phone", "9876543210"),
                Map.of("__row_number", 3, "name", "Ghost Lead", "email", "", "phone", "", "company", "Nowhere Corp")
        );

        DataQualityService.QualityAnalysisResult result = dataQualityService.analyzeAndValidate(rows);

        assertEquals(1, result.getProcessableRows().size(), "Only 1 row should be processable");
        assertEquals(1, result.getInitialErrors().size(), "1 row should be flagged as unprocessable error");
        assertEquals(3, result.getInitialErrors().get(0).getOriginalRow());
    }

    @Test
    @DisplayName("Should compute realistic data quality scores between 0 and 100")
    void testQualityScoring() {
        List<Map<String, Object>> rows = List.of(
                Map.of("__row_number", 2, "name", "Perfect Lead", "email", "good@domain.com", "phone", "9876543210", "company", "Acme Inc"),
                Map.of("__row_number", 3, "name", "", "email", "bad-email", "phone", "123", "company", "")
        );

        DataQualityService.QualityAnalysisResult result = dataQualityService.analyzeAndValidate(rows);

        Map<Integer, DataQualityItem> map = result.getQualityItemMap();
        assertTrue(map.get(2).getQualityScore() >= 90, "Complete lead should score >= 90");
        assertTrue(map.get(3).getQualityScore() < 60, "Defective lead should score < 60");
    }
}
