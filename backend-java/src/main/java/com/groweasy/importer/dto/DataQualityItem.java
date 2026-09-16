package com.groweasy.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataQualityItem {

    @JsonProperty("lead_id")
    private String leadId = "";

    @JsonProperty("original_row")
    private int originalRow;

    @JsonProperty("email")
    private String email = "";

    @JsonProperty("phone")
    private String phone = "";

    @JsonProperty("email_valid")
    private String emailValid = "MISSING";

    @JsonProperty("phone_valid")
    private String phoneValid = "MISSING";

    @JsonProperty("missing_fields")
    private String missingFields = "";

    @JsonProperty("duplicate")
    private boolean duplicate = false;

    @JsonProperty("duplicate_of")
    private String duplicateOf = "";

    @JsonProperty("quality_score")
    private int qualityScore = 100;

    @JsonProperty("validation_issues")
    private String validationIssues = "";

    public DataQualityItem() {
    }

    public String getLeadId() {
        return leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId != null ? leadId : "";
    }

    public int getOriginalRow() {
        return originalRow;
    }

    public void setOriginalRow(int originalRow) {
        this.originalRow = originalRow;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email : "";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone : "";
    }

    public String getEmailValid() {
        return emailValid;
    }

    public void setEmailValid(String emailValid) {
        this.emailValid = emailValid != null ? emailValid : "";
    }

    public String getPhoneValid() {
        return phoneValid;
    }

    public void setPhoneValid(String phoneValid) {
        this.phoneValid = phoneValid != null ? phoneValid : "";
    }

    public String getMissingFields() {
        return missingFields;
    }

    public void setMissingFields(String missingFields) {
        this.missingFields = missingFields != null ? missingFields : "";
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public String getDuplicateOf() {
        return duplicateOf;
    }

    public void setDuplicateOf(String duplicateOf) {
        this.duplicateOf = duplicateOf != null ? duplicateOf : "";
    }

    public int getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }

    public String getValidationIssues() {
        return validationIssues;
    }

    public void setValidationIssues(String validationIssues) {
        this.validationIssues = validationIssues != null ? validationIssues : "";
    }
}
