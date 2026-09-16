package com.groweasy.importer.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class DocxExtractionServiceTest {

    private final DocxExtractionService docxService = new DocxExtractionService();

    @Test
    @DisplayName("DocxExtractionService: Should extract paragraph text cleanly")
    void testParagraphExtraction() throws Exception {
        byte[] docxBytes;
        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFParagraph p1 = doc.createParagraph();
            XWPFRun r1 = p1.createRun();
            r1.setText("Customer: Vikram Malhotra");

            XWPFParagraph p2 = doc.createParagraph();
            XWPFRun r2 = p2.createRun();
            r2.setText("Email: vikram@zenith.in | Phone: +91 9820011223");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.write(baos);
            docxBytes = baos.toByteArray();
        }

        MockMultipartFile file = new MockMultipartFile(
                "file", "leads.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                docxBytes
        );

        String text = docxService.extractText(file);
        assertNotNull(text);
        assertTrue(text.contains("Vikram Malhotra"), "Extracted text must contain customer name");
        assertTrue(text.contains("vikram@zenith.in"), "Extracted text must contain email");
        assertTrue(text.contains("+91 9820011223"), "Extracted text must contain phone number");
    }

    @Test
    @DisplayName("DocxExtractionService: Should extract table structure cleanly")
    void testTableExtraction() throws Exception {
        byte[] docxBytes;
        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFTable table = doc.createTable(2, 3);
            XWPFTableRow header = table.getRow(0);
            header.getCell(0).setText("Name");
            header.getCell(1).setText("Email");
            header.getCell(2).setText("Mobile");

            XWPFTableRow row1 = table.getRow(1);
            row1.getCell(0).setText("Pooja Hegde");
            row1.getCell(1).setText("pooja@acme.com");
            row1.getCell(2).setText("9876543210");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.write(baos);
            docxBytes = baos.toByteArray();
        }

        MockMultipartFile file = new MockMultipartFile(
                "file", "table_leads.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                docxBytes
        );

        String text = docxService.extractText(file);
        assertNotNull(text);
        assertTrue(text.contains("--- TABLE START ---"), "Must contain table start marker");
        assertTrue(text.contains("Pooja Hegde | pooja@acme.com | 9876543210"), "Must contain pipe-separated table row");
        assertTrue(text.contains("--- TABLE END ---"), "Must contain table end marker");
    }

    @Test
    @DisplayName("DocxExtractionService: Should throw IllegalArgumentException on completely empty docx")
    void testEmptyDocx() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                new byte[0]
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            docxService.extractText(emptyFile);
        });
        assertTrue(ex.getMessage().contains("empty") || ex.getMessage().contains("no extractable text"));
    }

    @Test
    @DisplayName("DocxExtractionService: Should throw IllegalArgumentException on corrupted docx")
    void testCorruptedDocx() {
        MockMultipartFile corruptedFile = new MockMultipartFile(
                "file", "corrupt.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "This is not a zip or docx file content".getBytes(StandardCharsets.UTF_8)
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            docxService.extractText(corruptedFile);
        });
        assertTrue(ex.getMessage().contains("damaged") || ex.getMessage().contains("corrupted"));
    }
}
