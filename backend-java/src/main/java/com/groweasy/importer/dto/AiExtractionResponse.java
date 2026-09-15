package com.groweasy.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AiExtractionResponse {

    @JsonProperty("records")
    private List<CrmRecord> records = new ArrayList<>();

    @JsonProperty("skipped")
    private int skipped = 0;

    public AiExtractionResponse() {
    }

    public AiExtractionResponse(List<CrmRecord> records, int skipped) {
        this.records = records != null ? records : new ArrayList<>();
        this.skipped = skipped;
    }

    public List<CrmRecord> getRecords() {
        return records;
    }

    public void setRecords(List<CrmRecord> records) {
        this.records = records != null ? records : new ArrayList<>();
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }
}
