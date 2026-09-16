package com.groweasy.importer.service;

import com.groweasy.importer.dto.DataQualityItem;
import com.groweasy.importer.dto.ImportErrorItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class DataQualityService {

    private static final Logger log = LoggerFactory.getLogger(DataQualityService.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static class QualityAnalysisResult {
        private final List<Map<String, Object>> processableRows;
        private final Map<Integer, DataQualityItem> qualityItemMap; // mapped by original row
        private final List<DataQualityItem> allQualityItems;
        private final List<ImportErrorItem> initialErrors;
        private final int duplicateCount;
        private final int invalidEmailCount;
        private final int missingFieldCount;
        private final double averageQualityScore;

        public QualityAnalysisResult(
                List<Map<String, Object>> processableRows,
                Map<Integer, DataQualityItem> qualityItemMap,
                List<DataQualityItem> allQualityItems,
                List<ImportErrorItem> initialErrors,
                int duplicateCount,
                int invalidEmailCount,
                int missingFieldCount,
                double averageQualityScore) {
            this.processableRows = processableRows;
            this.qualityItemMap = qualityItemMap;
            this.allQualityItems = allQualityItems;
            this.initialErrors = initialErrors;
            this.duplicateCount = duplicateCount;
            this.invalidEmailCount = invalidEmailCount;
            this.missingFieldCount = missingFieldCount;
            this.averageQualityScore = averageQualityScore;
        }

        public List<Map<String, Object>> getProcessableRows() {
            return processableRows;
        }

        public Map<Integer, DataQualityItem> getQualityItemMap() {
            return qualityItemMap;
        }

        public List<DataQualityItem> getAllQualityItems() {
            return allQualityItems;
        }

        public List<ImportErrorItem> getInitialErrors() {
            return initialErrors;
        }

        public int getDuplicateCount() {
            return duplicateCount;
        }

        public int getInvalidEmailCount() {
            return invalidEmailCount;
        }

        public int getMissingFieldCount() {
            return missingFieldCount;
        }

        public double getAverageQualityScore() {
            return averageQualityScore;
        }
    }

    public QualityAnalysisResult analyzeAndValidate(List<Map<String, Object>> rawRows) {
        List<Map<String, Object>> processableRows = new ArrayList<>();
        Map<Integer, DataQualityItem> qualityItemMap = new LinkedHashMap<>();
        List<DataQualityItem> allQualityItems = new ArrayList<>();
        List<ImportErrorItem> initialErrors = new ArrayList<>();

        Map<String, Integer> seenEmails = new HashMap<>();
        Map<String, Integer> seenPhones = new HashMap<>();

        int duplicateCounter = 0;
        int invalidEmailCounter = 0;
        int missingFieldCounter = 0;
        int totalScoreAccumulator = 0;

        int leadIndex = 1;

        for (Map<String, Object> rawRow : rawRows) {
            int originalRow = rawRow.containsKey("__row_number")
                    ? ((Number) rawRow.get("__row_number")).intValue()
                    : leadIndex + 1;

            String leadId = String.format("LEAD-%04d", leadIndex++);

            // Extract candidate fields by header matching
            String rawName = extractValue(rawRow, "name", "full_name", "customer_name", "first_name", "lead_name");
            String rawEmail = extractValue(rawRow, "email", "e-mail", "email_address", "mail");
            String rawPhone = extractValue(rawRow, "phone", "mobile", "phone_number", "contact", "tel", "cell");
            String rawCompany = extractValue(rawRow, "company", "company_name", "organization", "business");
            String rawCity = extractValue(rawRow, "city", "location", "town");

            // Normalize fields
            String normName = rawName.trim();
            String normEmail = rawEmail.trim().toLowerCase();
            String normPhone = sanitizePhone(rawPhone);
            String normCompany = rawCompany.trim();

            DataQualityItem item = new DataQualityItem();
            item.setLeadId(leadId);
            item.setOriginalRow(originalRow);
            item.setEmail(normEmail.isEmpty() ? rawEmail.trim() : normEmail);
            item.setPhone(normPhone.isEmpty() ? rawPhone.trim() : normPhone);

            List<String> issues = new ArrayList<>();
            List<String> missing = new ArrayList<>();
            int score = 100;

            // 1. Name Check
            if (normName.isEmpty()) {
                missing.add("name");
                issues.add("Missing contact name");
                score -= 15;
                missingFieldCounter++;
            }

            // 2. Email Validation
            if (normEmail.isEmpty()) {
                item.setEmailValid("MISSING");
                missing.add("email");
                score -= 25;
                missingFieldCounter++;
            } else if (EMAIL_PATTERN.matcher(normEmail).matches()) {
                item.setEmailValid("VALID");
            } else {
                item.setEmailValid("INVALID");
                issues.add("Invalid email syntax: '" + normEmail + "'");
                score -= 20;
                invalidEmailCounter++;
            }

            // 3. Phone Validation
            if (normPhone.isEmpty()) {
                item.setPhoneValid("MISSING");
                missing.add("phone");
                score -= 20;
                missingFieldCounter++;
            } else {
                String digits = normPhone.replaceAll("[^0-9]", "");
                if (digits.length() >= 7 && digits.length() <= 15) {
                    item.setPhoneValid("VALID");
                } else {
                    item.setPhoneValid("INVALID");
                    issues.add("Non-standard phone length (" + digits.length() + " digits)");
                    score -= 15;
                }
            }

            // 4. Company Check
            if (normCompany.isEmpty()) {
                missing.add("company");
                score -= 10;
            }

            // 5. Duplicate Detection
            boolean isDuplicate = false;
            String duplicateOf = "";

            if (!normEmail.isEmpty() && item.getEmailValid().equals("VALID")) {
                if (seenEmails.containsKey(normEmail)) {
                    isDuplicate = true;
                    duplicateOf = "Row " + seenEmails.get(normEmail);
                    issues.add("Duplicate email (seen on " + duplicateOf + ")");
                } else {
                    seenEmails.put(normEmail, originalRow);
                }
            }

            if (!normPhone.isEmpty() && item.getPhoneValid().equals("VALID")) {
                String phoneDigits = normPhone.replaceAll("[^0-9]", "");
                if (phoneDigits.length() >= 7) {
                    String phoneKey = phoneDigits.length() >= 10
                            ? phoneDigits.substring(phoneDigits.length() - 10)
                            : phoneDigits;
                    if (seenPhones.containsKey(phoneKey)) {
                        isDuplicate = true;
                        String phoneDupOf = "Row " + seenPhones.get(phoneKey);
                        if (duplicateOf.isEmpty()) {
                            duplicateOf = phoneDupOf;
                        }
                        issues.add("Duplicate phone (seen on " + phoneDupOf + ")");
                    } else {
                        seenPhones.put(phoneKey, originalRow);
                    }
                }
            }

            if (isDuplicate) {
                item.setDuplicate(true);
                item.setDuplicateOf(duplicateOf);
                score -= 20;
                duplicateCounter++;
            }

            // Clamp score
            score = Math.max(0, Math.min(100, score));
            item.setQualityScore(score);
            item.setMissingFields(String.join(", ", missing));
            item.setValidationIssues(String.join("; ", issues));

            totalScoreAccumulator += score;
            allQualityItems.add(item);
            qualityItemMap.put(originalRow, item);

            // Determine if processable
            boolean hasEmail = !normEmail.isEmpty();
            boolean hasPhone = !normPhone.isEmpty();

            if (!hasEmail && !hasPhone) {
                // Rule: If email and mobile both missing, cannot create CRM contact
                ImportErrorItem error = new ImportErrorItem(
                        originalRow,
                        normName.isEmpty() ? "Row " + originalRow : normName,
                        "Missing Required Contact Info",
                        "Both email and phone number are missing. At least one contact channel is required.",
                        "Provide either a valid email address or phone number to import this lead."
                );
                initialErrors.add(error);
                log.info("Row {} marked unprocessable: both email and phone missing.", originalRow);
            } else {
                // Keep for Gemini AI processing
                // Also enrich the row map with normalized fields and leadId
                Map<String, Object> enrichedRow = new LinkedHashMap<>(rawRow);
                enrichedRow.put("__lead_id", leadId);
                enrichedRow.put("__normalized_email", normEmail);
                enrichedRow.put("__normalized_phone", normPhone);
                processableRows.add(enrichedRow);
            }
        }

        double averageScore = rawRows.isEmpty() ? 100.0 : (double) totalScoreAccumulator / rawRows.size();

        return new QualityAnalysisResult(
                processableRows,
                qualityItemMap,
                allQualityItems,
                initialErrors,
                duplicateCounter,
                invalidEmailCounter,
                missingFieldCounter,
                Math.round(averageScore * 10.0) / 10.0
        );
    }

    private String extractValue(Map<String, Object> row, String... aliases) {
        for (String alias : aliases) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String key = entry.getKey().trim().toLowerCase().replaceAll("[ _-]", "");
                String target = alias.toLowerCase().replaceAll("[ _-]", "");
                if (key.equals(target)) {
                    Object val = entry.getValue();
                    return val != null ? val.toString().trim() : "";
                }
            }
        }
        return "";
    }

    private String sanitizePhone(String phone) {
        if (phone == null || phone.isBlank()) return "";
        // Remove spaces, dashes, brackets, but keep leading + if present
        String trimmed = phone.trim();
        boolean hasPlus = trimmed.startsWith("+");
        String digits = trimmed.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return "";
        return hasPlus ? "+" + digits : digits;
    }
}
