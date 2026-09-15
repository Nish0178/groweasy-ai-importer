package com.groweasy.importer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImportResponse {

    @JsonProperty("success")
    private boolean success;

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

    public ImportResponse() {
    }

    public static ImportResponse success(int totalImported, int totalSkipped, List<CrmRecord> records, String downloadUrl) {
        ImportResponse resp = new ImportResponse();
        resp.setSuccess(true);
        resp.setTotalImported(totalImported);
        resp.setTotalSkipped(totalSkipped);
        resp.setRecords(records);
        resp.setDownloadUrl(downloadUrl);
        return resp;
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
    }

    public int getTotalSkipped() {
        return totalSkipped;
    }

    public void setTotalSkipped(int totalSkipped) {
        this.totalSkipped = totalSkipped;
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
}
