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
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.model:gemini-2.5-flash}") String model,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
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
                log.error("Batch {} failed to process via Gemini: {}", batchNumber, e.getMessage(), e);
                totalSkipped += batch.size();
            }
        }

        return new AiExtractionResponse(finalRecords, totalSkipped);
    }

    /**
     * Sends a single batch to Gemini API and parses the JSON extraction result.
     */
    public AiExtractionResponse extractCrmBatch(List<Map<String, Object>> batch) throws Exception {
        String batchJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(batch);
        String prompt = CrmExtractionPrompt.PROMPT + "\n\nCSV Records:\n\n" + batchJson + "\n";

        // Build Google Generative AI request payload
        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> contentPart = Map.of("parts", List.of(textPart));
        Map<String, Object> requestBodyMap = Map.of("contents", List.of(contentPart));

        String requestJson = objectMapper.writeValueAsString(requestBodyMap);

        String endpoint = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                model, apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            log.error("Gemini API error response HTTP {}: {}", response.statusCode(), response.body());
            throw new RuntimeException("Gemini API call returned status: " + response.statusCode());
        }

        String responseBody = response.body();
        log.debug("Gemini response received.");

        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode candidatesNode = rootNode.path("candidates");

        if (candidatesNode.isEmpty()) {
            throw new RuntimeException("No candidates returned from Gemini.");
        }

        JsonNode textNode = candidatesNode.get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text");

        if (textNode.isMissingNode()) {
            throw new RuntimeException("No text in Gemini candidate part.");
        }

        String rawText = textNode.asText();
        log.info("========== GEMINI RAW TEXT EXTRACT ==========\n{}", rawText);

        // Sanitize markdown fences ```json ... ```
        String cleanedText = rawText
                .replaceAll("(?i)```json", "")
                .replaceAll("```", "")
                .trim();

        // Find outer JSON boundaries
        int start = cleanedText.indexOf("{");
        int end = cleanedText.lastIndexOf("}");

        if (start == -1 || end == -1 || end <= start) {
            throw new RuntimeException("Gemini did not return valid JSON object.");
        }

        String jsonPayload = cleanedText.substring(start, end + 1);
        AiExtractionResponse parsed = objectMapper.readValue(jsonPayload, AiExtractionResponse.class);

        if (parsed.getRecords() == null) {
            parsed.setRecords(new ArrayList<>());
        }

        return parsed;
    }
}
