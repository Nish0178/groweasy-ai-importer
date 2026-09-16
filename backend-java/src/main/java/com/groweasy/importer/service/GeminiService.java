package com.groweasy.importer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groweasy.importer.dto.AiExtractionResponse;
import com.groweasy.importer.dto.CrmRecord;
import com.groweasy.importer.dto.ImportErrorItem;
import com.groweasy.importer.prompt.CrmExtractionPrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);
    private static final int BATCH_SIZE = 20;

    private final String apiKey;
    private final String model;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public GeminiService(
            @Value("${gemini.api.key:}") String apiKey,
            @Value("${gemini.api.model:gemini-2.5-flash}") String model,
            ObjectMapper objectMapper) {
        this.apiKey = resolveApiKey(apiKey);
        this.model = (model != null && !model.isBlank()) ? model.trim() : "gemini-2.5-flash";
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        if (this.apiKey.isEmpty()) {
            log.warn("Gemini API key is not configured. Calls to AI extraction will fail until configured.");
        } else {
            log.info("Gemini API configured with model '{}' (API key present).", this.model);
        }
    }

    public boolean isConfigured() {
        return this.apiKey != null && !this.apiKey.isBlank();
    }

    private String resolveApiKey(String propertyKey) {
        // 1. Check Spring property injection
        if (propertyKey != null && !propertyKey.isBlank()) {
            return propertyKey.trim();
        }

        // 2. Check System process environment
        String envKey = System.getenv("GEMINI_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            return envKey.trim();
        }

        // 3. Check System properties
        String sysPropKey = System.getProperty("gemini.api.key", System.getProperty("GEMINI_API_KEY"));
        if (sysPropKey != null && !sysPropKey.isBlank()) {
            return sysPropKey.trim();
        }

        // 4. Fallback: Check local .env files
        List<Path> candidateEnvFiles = List.of(
                Paths.get(".env"),
                Paths.get("backend-java", ".env"),
                Paths.get("..", ".env"),
                Paths.get("..", "backend", ".env"),
                Paths.get("backend", ".env")
        );

        for (Path envPath : candidateEnvFiles) {
            try {
                if (Files.exists(envPath) && Files.isRegularFile(envPath)) {
                    List<String> lines = Files.readAllLines(envPath, StandardCharsets.UTF_8);
                    for (String line : lines) {
                        String trimmed = line.trim();
                        if (trimmed.startsWith("GEMINI_API_KEY=")) {
                            String value = trimmed.substring("GEMINI_API_KEY=".length()).trim();
                            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                                (value.startsWith("'") && value.endsWith("'"))) {
                                value = value.substring(1, value.length() - 1).trim();
                            }
                            if (!value.isEmpty()) {
                                log.info("Loaded GEMINI_API_KEY from environment file: {}", envPath.toAbsolutePath().normalize());
                                return value;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("Could not read env file {}: {}", envPath, e.getMessage());
            }
        }

        return "";
    }

    public static class GeminiExtractionResult {
        private final List<CrmRecord> records;
        private final List<ImportErrorItem> errors;

        public GeminiExtractionResult(List<CrmRecord> records, List<ImportErrorItem> errors) {
            this.records = records != null ? records : new ArrayList<>();
            this.errors = errors != null ? errors : new ArrayList<>();
        }

        public List<CrmRecord> getRecords() {
            return records;
        }

        public List<ImportErrorItem> getErrors() {
            return errors;
        }
    }

    /**
     * Processes CSV records in batches with full error diagnostics and original row correlation.
     */
    public GeminiExtractionResult processRecordsWithDiagnostics(List<Map<String, Object>> records) {
        if (records == null || records.isEmpty()) {
            return new GeminiExtractionResult(Collections.emptyList(), Collections.emptyList());
        }

        List<CrmRecord> finalRecords = new ArrayList<>();
        List<ImportErrorItem> batchErrors = new ArrayList<>();

        int totalBatches = (int) Math.ceil((double) records.size() / BATCH_SIZE);

        for (int i = 0; i < records.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, records.size());
            List<Map<String, Object>> batch = records.subList(i, endIndex);

            int batchNumber = (i / BATCH_SIZE) + 1;
            log.info("Processing Gemini batch {}/{} ({} records)", batchNumber, totalBatches, batch.size());

            try {
                AiExtractionResponse result = extractCrmBatch(batch);
                if (result != null && result.getRecords() != null) {
                    List<CrmRecord> extracted = result.getRecords();
                    for (int j = 0; j < extracted.size(); j++) {
                        CrmRecord rec = extracted.get(j);
                        if (j < batch.size()) {
                            Map<String, Object> inputRow = batch.get(j);
                            if (rec.getOriginalRow() == 0 && inputRow.containsKey("__row_number")) {
                                rec.setOriginalRow(((Number) inputRow.get("__row_number")).intValue());
                            }
                            if ((rec.getLeadId() == null || rec.getLeadId().isBlank()) && inputRow.containsKey("__lead_id")) {
                                rec.setLeadId((String) inputRow.get("__lead_id"));
                            }
                        }
                        if ((rec.getPhone() == null || rec.getPhone().isBlank()) &&
                                rec.getMobileWithoutCountryCode() != null && !rec.getMobileWithoutCountryCode().isBlank()) {
                            String code = (rec.getCountryCode() != null && !rec.getCountryCode().isBlank()) ? rec.getCountryCode() + " " : "";
                            rec.setPhone(code + rec.getMobileWithoutCountryCode());
                        }
                        finalRecords.add(rec);
                    }
                }
            } catch (Exception e) {
                log.error("Batch {}/{} failed in Gemini extraction: {}", batchNumber, totalBatches, e.getMessage());
                for (Map<String, Object> inputRow : batch) {
                    int rowNum = inputRow.containsKey("__row_number") ? ((Number) inputRow.get("__row_number")).intValue() : 0;
                    String identifier = inputRow.containsKey("Name") ? inputRow.get("Name").toString()
                            : inputRow.containsKey("name") ? inputRow.get("name").toString()
                            : "Row " + rowNum;
                    batchErrors.add(new ImportErrorItem(
                            rowNum,
                            identifier,
                            "AI Processing Failure",
                            e.getMessage() != null ? e.getMessage() : "Gemini extraction failed",
                            "Retry batch or inspect data encoding."
                    ));
                }
            }
        }

        return new GeminiExtractionResult(finalRecords, batchErrors);
    }

    /**
     * Backward-compatible batch processor.
     */
    public AiExtractionResponse processCsvRecords(List<Map<String, Object>> records) {
        GeminiExtractionResult res = processRecordsWithDiagnostics(records);
        return new AiExtractionResponse(res.getRecords(), res.getErrors().size());
    }

    /**
     * Sends a single batch to Gemini API and parses the JSON extraction result.
     */
    public AiExtractionResponse extractCrmBatch(List<Map<String, Object>> batch) throws Exception {
        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY environment variable is not configured. Please set GEMINI_API_KEY before running the server.");
        }

        String batchJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(batch);
        String prompt = CrmExtractionPrompt.PROMPT + "\n\nCSV Records:\n\n" + batchJson + "\n";

        // Build Google Generative AI request payload
        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> contentPart = Map.of("parts", List.of(textPart));
        Map<String, Object> requestBodyMap = Map.of("contents", List.of(contentPart));

        String requestJson = objectMapper.writeValueAsString(requestBodyMap);

        String endpoint = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent",
                model);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", this.apiKey)
                .timeout(Duration.ofSeconds(120))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            log.error("Gemini API error HTTP {}: {}", response.statusCode(), response.body());
            throw new RuntimeException("Gemini API call returned status: " + response.statusCode());
        }

        return parseGeminiResponse(response.body());
    }

    /**
     * Extracts CRM leads from raw unstructured/tabular text extracted from a business document (e.g. DOCX).
     */
    public GeminiExtractionResult processDocumentTextWithDiagnostics(String documentText) {
        if (documentText == null || documentText.isBlank()) {
            return new GeminiExtractionResult(Collections.emptyList(), Collections.emptyList());
        }

        List<CrmRecord> finalRecords = new ArrayList<>();
        List<ImportErrorItem> errors = new ArrayList<>();

        try {
            AiExtractionResponse result = extractCrmFromDocumentText(documentText);
            if (result != null && result.getRecords() != null) {
                List<CrmRecord> extracted = result.getRecords();
                for (int j = 0; j < extracted.size(); j++) {
                    CrmRecord rec = extracted.get(j);
                    if (rec.getOriginalRow() <= 0) {
                        rec.setOriginalRow(j + 1);
                    }
                    if (rec.getLeadId() == null || rec.getLeadId().isBlank()) {
                        rec.setLeadId(String.format("LEAD-%04d", rec.getOriginalRow()));
                    }
                    if ((rec.getPhone() == null || rec.getPhone().isBlank()) &&
                            rec.getMobileWithoutCountryCode() != null && !rec.getMobileWithoutCountryCode().isBlank()) {
                        String code = (rec.getCountryCode() != null && !rec.getCountryCode().isBlank()) ? rec.getCountryCode() + " " : "";
                        rec.setPhone(code + rec.getMobileWithoutCountryCode());
                    }
                    finalRecords.add(rec);
                }
            }
        } catch (Exception e) {
            log.error("Failed in Gemini document text extraction: {}", e.getMessage());
            errors.add(new ImportErrorItem(
                    1,
                    "Document Content",
                    "AI Processing Failure",
                    e.getMessage() != null ? e.getMessage() : "Gemini document extraction failed",
                    "Verify document format or retry extraction."
            ));
        }

        return new GeminiExtractionResult(finalRecords, errors);
    }

    /**
     * Sends document text to Gemini API using DOCUMENT_EXTRACTION_PROMPT.
     */
    public AiExtractionResponse extractCrmFromDocumentText(String documentText) throws Exception {
        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY environment variable is not configured. Please set GEMINI_API_KEY before running the server.");
        }

        String prompt = CrmExtractionPrompt.DOCUMENT_EXTRACTION_PROMPT + "\n\nDocument Content:\n\n" + documentText + "\n";

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> contentPart = Map.of("parts", List.of(textPart));
        Map<String, Object> requestBodyMap = Map.of("contents", List.of(contentPart));

        String requestJson = objectMapper.writeValueAsString(requestBodyMap);

        String endpoint = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent",
                model);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", this.apiKey)
                .timeout(Duration.ofSeconds(120))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            log.error("Gemini API error HTTP {}: {}", response.statusCode(), response.body());
            throw new RuntimeException("Gemini API call returned status: " + response.statusCode());
        }

        return parseGeminiResponse(response.body());
    }

    private AiExtractionResponse parseGeminiResponse(String responseBody) throws Exception {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode candidatesNode = rootNode.path("candidates");

        if (candidatesNode.isEmpty()) {
            throw new RuntimeException("No candidates returned from Gemini. Response: " + responseBody);
        }

        StringBuilder fullText = new StringBuilder();
        JsonNode partsNode = candidatesNode.get(0).path("content").path("parts");
        if (partsNode.isArray()) {
            for (JsonNode part : partsNode) {
                if (part.has("text")) {
                    fullText.append(part.path("text").asText());
                }
            }
        }

        if (fullText.isEmpty()) {
            throw new RuntimeException("No text in Gemini candidate parts.");
        }

        String rawText = fullText.toString();
        log.info("========== GEMINI RAW TEXT EXTRACT ==========\n{}", rawText);

        // Sanitize markdown fences ```json ... ```
        String cleanedText = rawText
                .replaceAll("(?i)```json", "")
                .replaceAll("```", "")
                .trim();

        // Extract JSON boundaries
        int startObj = cleanedText.indexOf("{");
        int endObj = cleanedText.lastIndexOf("}");
        int startArr = cleanedText.indexOf("[");
        int endArr = cleanedText.lastIndexOf("]");

        AiExtractionResponse parsed;
        if (startObj != -1 && endObj != -1 && (startArr == -1 || startObj < startArr)) {
            String jsonPayload = cleanedText.substring(startObj, endObj + 1);
            parsed = objectMapper.readValue(jsonPayload, AiExtractionResponse.class);
        } else if (startArr != -1 && endArr != -1) {
            String jsonPayload = cleanedText.substring(startArr, endArr + 1);
            List<CrmRecord> records = objectMapper.readValue(
                    jsonPayload,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CrmRecord.class)
            );
            parsed = new AiExtractionResponse(records, 0);
        } else {
            throw new RuntimeException("Gemini did not return a valid JSON payload: " + cleanedText);
        }

        if (parsed.getRecords() == null) {
            parsed.setRecords(new ArrayList<>());
        }

        return parsed;
    }
}
