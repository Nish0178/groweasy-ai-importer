package com.groweasy.importer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groweasy.importer.dto.AiExtractionResponse;
import com.groweasy.importer.dto.CrmRecord;
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

    /**
     * Processes full CSV records list in batches of 20 and combines extracted CRM records.
     */
    public AiExtractionResponse processCsvRecords(List<Map<String, Object>> records) {
        if (records == null || records.isEmpty()) {
            return new AiExtractionResponse(Collections.emptyList(), 0);
        }

        List<CrmRecord> finalRecords = new ArrayList<>();
        int totalSkipped = 0;

        int totalBatches = (int) Math.ceil((double) records.size() / BATCH_SIZE);

        for (int i = 0; i < records.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, records.size());
            List<Map<String, Object>> batch = records.subList(i, endIndex);

            int batchNumber = (i / BATCH_SIZE) + 1;
            log.info("Processing batch {}/{} ({} records)", batchNumber, totalBatches, batch.size());

            try {
                AiExtractionResponse result = extractCrmBatch(batch);
                if (result != null && result.getRecords() != null) {
                    finalRecords.addAll(result.getRecords());
                    totalSkipped += result.getSkipped();
                } else {
                    totalSkipped += batch.size();
                }
            } catch (Exception e) {
                log.error("Batch {} failed to process via Gemini: {}", batchNumber, e.getMessage());
                totalSkipped += batch.size();
                if (e instanceof IllegalStateException || (e.getMessage() != null && (e.getMessage().contains("403") || e.getMessage().contains("401")))) {
                    throw new RuntimeException("Gemini API extraction failed: " + e.getMessage(), e);
                }
            }
        }

        return new AiExtractionResponse(finalRecords, totalSkipped);
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
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            log.error("Gemini API error HTTP {}: {}", response.statusCode(), response.body());
            throw new RuntimeException("Gemini API call returned status: " + response.statusCode());
        }

        String responseBody = response.body();
        log.debug("Gemini response received.");

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
