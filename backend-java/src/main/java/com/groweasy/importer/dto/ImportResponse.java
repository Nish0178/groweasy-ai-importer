package com.groweasy.importer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImportResponse {

    @JsonProperty("success")
    private boolean success;

    // Backward compatibility aliases
    @JsonProperty("totalImported")
    private int totalImported;

    @JsonProperty("totalSkipped")
    private int totalSkipped;

    @JsonProperty("records")
    private List<CrmRecord> records = new ArrayList<>();

    @JsonProperty("downloadUrl")
    private String downloadUrl;

    @JsonProperty("message")
    private String message;

    // Professional Intelligence Metrics
    @JsonProperty("totalRecords")
    private int totalRecords;

    @JsonProperty("processedRecords")
    private int processedRecords;

    @JsonProperty("skippedRecords")
    private int skippedRecords;

    @JsonProperty("processingPercentage")
    private double processingPercentage;

    @JsonProperty("duplicateCount")
    private int duplicateCount;

    @JsonProperty("invalidEmailCount")
    private int invalidEmailCount;

    @JsonProperty("missingFieldCount")
    private int missingFieldCount;

    @JsonProperty("averageQualityScore")
    private double averageQualityScore;

    @JsonProperty("leadStatusDistribution")
    private Map<String, Integer> leadStatusDistribution = new LinkedHashMap<>();

    @JsonProperty("dataQualitySummary")
    private Map<String, Object> dataQualitySummary = new LinkedHashMap<>();

    @JsonProperty("errors")
    private List<ImportErrorItem> errors = new ArrayList<>();

    public ImportResponse() {
    }

    public static ImportResponse error(String message) {
        ImportResponse resp = new ImportResponse();
        resp.setSuccess(false);
        resp.setMessage(message);
        return resp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTotalImported() {
        return totalImported;
    }

    public void setTotalImported(int totalImported) {
        this.totalImported = totalImported;
        this.processedRecords = totalImported;
    }

    public int getTotalSkipped() {
        return totalSkipped;
    }

    public void setTotalSkipped(int totalSkipped) {
        this.totalSkipped = totalSkipped;
        this.skippedRecords = totalSkipped;
    }

    public List<CrmRecord> getRecords() {
        return records;
    }

    public void setRecords(List<CrmRecord> records) {
        this.records = records != null ? records : new ArrayList<>();
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getProcessedRecords() {
        return processedRecords;
    }

    public void setProcessedRecords(int processedRecords) {
        this.processedRecords = processedRecords;
        this.totalImported = processedRecords;
    }

    public int getSkippedRecords() {
        return skippedRecords;
    }

    public void setSkippedRecords(int skippedRecords) {
        this.skippedRecords = skippedRecords;
        this.totalSkipped = skippedRecords;
    }

    public double getProcessingPercentage() {
        return processingPercentage;
    }

    public void setProcessingPercentage(double processingPercentage) {
        this.processingPercentage = processingPercentage;
    }

    public int getDuplicateCount() {
        return duplicateCount;
    }

    public void setDuplicateCount(int duplicateCount) {
        this.duplicateCount = duplicateCount;
    }

    public int getInvalidEmailCount() {
        return invalidEmailCount;
    }

    public void setInvalidEmailCount(int invalidEmailCount) {
        this.invalidEmailCount = invalidEmailCount;
    }

    public int getMissingFieldCount() {
        return missingFieldCount;
    }

    public void setMissingFieldCount(int missingFieldCount) {
        this.missingFieldCount = missingFieldCount;
    }

    public double getAverageQualityScore() {
        return averageQualityScore;
    }

    public void setAverageQualityScore(double averageQualityScore) {
        this.averageQualityScore = averageQualityScore;
    }

    public Map<String, Integer> getLeadStatusDistribution() {
        return leadStatusDistribution;
    }

    public void setLeadStatusDistribution(Map<String, Integer> leadStatusDistribution) {
        this.leadStatusDistribution = leadStatusDistribution != null ? leadStatusDistribution : new LinkedHashMap<>();
    }

    public Map<String, Object> getDataQualitySummary() {
        return dataQualitySummary;
    }

    public void setDataQualitySummary(Map<String, Object> dataQualitySummary) {
        this.dataQualitySummary = dataQualitySummary != null ? dataQualitySummary : new LinkedHashMap<>();
    }

    public List<ImportErrorItem> getErrors() {
        return errors;
    }

    public void setErrors(List<ImportErrorItem> errors) {
        this.errors = errors != null ? errors : new ArrayList<>();
    }
}
