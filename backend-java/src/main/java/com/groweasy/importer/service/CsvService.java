package com.groweasy.importer.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CsvService {

    private static final Logger log = LoggerFactory.getLogger(CsvService.class);

    /**
     * Parses a CSV file into a list of row key-value maps.
     * Automatically detects standard comma, semicolon, or tab delimiters.
     */
    public List<Map<String, Object>> parseCsv(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        String content = new String(bytes, StandardCharsets.UTF_8);

        // Remove BOM if present
        if (content.startsWith("\uFEFF")) {
            content = content.substring(1);
        }

        if (content.trim().isEmpty()) {
            return Collections.emptyList();
        }

        char delimiter = detectDelimiter(content);
        log.info("Detected delimiter '{}' for file: {}", delimiter, file.getOriginalFilename());

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(delimiter)
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .setAllowMissingColumnNames(true)
                .build();

        List<Map<String, Object>> rows = new ArrayList<>();

        try (CSVParser parser = new CSVParser(new StringReader(content), csvFormat)) {
            Map<String, Integer> headerMap = parser.getHeaderMap();
            if (headerMap == null || headerMap.isEmpty()) {
                throw new IllegalArgumentException("CSV does not contain valid headers.");
            }

            for (CSVRecord record : parser) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
                    String header = entry.getKey();
                    if (header != null && !header.isBlank()) {
                        int colIndex = entry.getValue();
                        String value = colIndex < record.size() ? record.get(colIndex) : "";
                        row.put(header, value != null ? value.trim() : "");
                    }
                }
                if (!row.isEmpty()) {
                    rows.add(row);
                }
            }
        }

        log.info("Successfully parsed {} rows from CSV.", rows.size());
        return rows;
    }

    private char detectDelimiter(String content) {
        String firstLine = "";
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            firstLine = reader.readLine();
        } catch (Exception e) {
            // fallback
        }

        if (firstLine == null) return ',';

        int commas = countOccurrences(firstLine, ',');
        int semicolons = countOccurrences(firstLine, ';');
        int tabs = countOccurrences(firstLine, '\t');

        if (semicolons > commas && semicolons > tabs) return ';';
        if (tabs > commas && tabs > semicolons) return '\t';
        return ',';
    }

    private int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) count++;
        }
        return count;
    }
}
