package com.groweasy.importer.service;

import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocxExtractionService {

    private static final Logger log = LoggerFactory.getLogger(DocxExtractionService.class);

    /**
     * Extracts text, paragraphs, and tables from an uploaded DOCX file.
     *
     * @param file the uploaded MultipartFile representing a Word document (.docx)
     * @return clean, normalized extracted text representing document content
     * @throws IllegalArgumentException if the file is empty, corrupted, or has no extractable text
     */
    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("The uploaded DOCX file is empty or contains no extractable text.");
        }

        StringBuilder contentBuilder = new StringBuilder();

        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {

            List<IBodyElement> bodyElements = document.getBodyElements();

            if (bodyElements != null && !bodyElements.isEmpty()) {
                for (IBodyElement element : bodyElements) {
                    if (element.getElementType() == BodyElementType.PARAGRAPH) {
                        XWPFParagraph paragraph = (XWPFParagraph) element;
                        String text = paragraph.getText();
                        if (text != null && !text.isBlank()) {
                            contentBuilder.append(text.trim()).append("\n");
                        }
                    } else if (element.getElementType() == BodyElementType.TABLE) {
                        XWPFTable table = (XWPFTable) element;
                        appendTableText(contentBuilder, table);
                    }
                }
            } else {
                // Defensive fallback: inspect paragraphs and tables separately
                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    String text = paragraph.getText();
                    if (text != null && !text.isBlank()) {
                        contentBuilder.append(text.trim()).append("\n");
                    }
                }
                for (XWPFTable table : document.getTables()) {
                    appendTableText(contentBuilder, table);
                }
            }

        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("The uploaded DOCX file")) {
                throw e;
            }
            log.error("Failed to parse DOCX file {}: {}", file.getOriginalFilename(), e.getMessage());
            throw new IllegalArgumentException(
                    "Failed to read DOCX document. The file may be damaged, corrupted, or not a valid Word document.");
        } catch (Exception e) {
            log.error("Failed to parse DOCX file {}: {}", file.getOriginalFilename(), e.getMessage());
            throw new IllegalArgumentException(
                    "Failed to read DOCX document. The file may be damaged, corrupted, or not a valid Word document.");
        }

        String rawContent = contentBuilder.toString().trim();
        if (rawContent.isBlank()) {
            throw new IllegalArgumentException("The uploaded DOCX file is empty or contains no extractable text.");
        }

        // Clean & normalize consecutive blank lines
        String cleaned = rawContent.replaceAll("(?m)^[ \t]*\r?\n{2,}", "\n\n");
        log.info("Successfully extracted {} characters from DOCX: {}", cleaned.length(), file.getOriginalFilename());
        return cleaned;
    }

    private void appendTableText(StringBuilder sb, XWPFTable table) {
        if (table == null || table.getRows() == null || table.getRows().isEmpty()) {
            return;
        }

        sb.append("\n--- TABLE START ---\n");
        for (XWPFTableRow row : table.getRows()) {
            List<String> cellValues = new ArrayList<>();
            for (XWPFTableCell cell : row.getTableCells()) {
                String cellText = cell.getText();
                cellValues.add(cellText != null ? cellText.trim() : "");
            }
            sb.append(String.join(" | ", cellValues)).append("\n");
        }
        sb.append("--- TABLE END ---\n\n");
    }
}
