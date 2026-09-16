package com.groweasy.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportErrorItem {

    @JsonProperty("original_row")
    private int originalRow;

    @JsonProperty("record_identifier")
    private String recordIdentifier = "";

    @JsonProperty("error_type")
    private String errorType = "";

    @JsonProperty("error_message")
    private String errorMessage = "";

    @JsonProperty("recommended_action")
    private String recommendedAction = "";

    public ImportErrorItem() {
    }

    public ImportErrorItem(int originalRow, String recordIdentifier, String errorType, String errorMessage, String recommendedAction) {
        this.originalRow = originalRow;
        this.recordIdentifier = recordIdentifier != null ? recordIdentifier : "";
        this.errorType = errorType != null ? errorType : "";
        this.errorMessage = errorMessage != null ? errorMessage : "";
        this.recommendedAction = recommendedAction != null ? recommendedAction : "";
    }

    public int getOriginalRow() {
        return originalRow;
    }

    public void setOriginalRow(int originalRow) {
        this.originalRow = originalRow;
    }

    public String getRecordIdentifier() {
        return recordIdentifier;
    }

    public void setRecordIdentifier(String recordIdentifier) {
        this.recordIdentifier = recordIdentifier != null ? recordIdentifier : "";
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType != null ? errorType : "";
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage != null ? errorMessage : "";
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction != null ? recommendedAction : "";
    }
}
