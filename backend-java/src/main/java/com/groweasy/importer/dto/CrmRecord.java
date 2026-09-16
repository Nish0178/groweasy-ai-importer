package com.groweasy.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CrmRecord {

    @JsonProperty("lead_id")
    private String leadId = "";

    @JsonProperty("original_row")
    private int originalRow = 0;

    @JsonProperty("created_at")
    private String createdAt = "";

    @JsonProperty("name")
    private String name = "";

    @JsonProperty("email")
    private String email = "";

    @JsonProperty("phone")
    private String phone = "";

    @JsonProperty("country_code")
    private String countryCode = "";

    @JsonProperty("mobile_without_country_code")
    private String mobileWithoutCountryCode = "";

    @JsonProperty("company")
    private String company = "";

    @JsonProperty("job_title")
    private String jobTitle = "";

    @JsonProperty("industry")
    private String industry = "";

    @JsonProperty("website")
    private String website = "";

    @JsonProperty("city")
    private String city = "";

    @JsonProperty("state")
    private String state = "";

    @JsonProperty("country")
    private String country = "";

    @JsonProperty("lead_owner")
    private String leadOwner = "";

    @JsonProperty("crm_status")
    private String crmStatus = "";

    @JsonProperty("priority")
    private String priority = "";

    @JsonProperty("ai_insight")
    private String aiInsight = "";

    @JsonProperty("recommended_action")
    private String recommendedAction = "";

    @JsonProperty("ai_reason")
    private String aiReason = "";

    @JsonProperty("crm_note")
    private String crmNote = "";

    @JsonProperty("data_source")
    private String dataSource = "";

    @JsonProperty("possession_time")
    private String possessionTime = "";

    @JsonProperty("description")
    private String description = "";

    @JsonProperty("quality_score")
    private int qualityScore = 100;

    @JsonProperty("validation_issues")
    private String validationIssues = "";

    public CrmRecord() {
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt != null ? createdAt : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name : "";
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode != null ? countryCode : "";
    }

    public String getMobileWithoutCountryCode() {
        return mobileWithoutCountryCode;
    }

    public void setMobileWithoutCountryCode(String mobileWithoutCountryCode) {
        this.mobileWithoutCountryCode = mobileWithoutCountryCode != null ? mobileWithoutCountryCode : "";
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company != null ? company : "";
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle != null ? jobTitle : "";
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry != null ? industry : "";
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website != null ? website : "";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city != null ? city : "";
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state != null ? state : "";
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country != null ? country : "";
    }

    public String getLeadOwner() {
        return leadOwner;
    }

    public void setLeadOwner(String leadOwner) {
        this.leadOwner = leadOwner != null ? leadOwner : "";
    }

    public String getCrmStatus() {
        return crmStatus;
    }

    public void setCrmStatus(String crmStatus) {
        this.crmStatus = crmStatus != null ? crmStatus : "";
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority != null ? priority : "";
    }

    public String getAiInsight() {
        return aiInsight;
    }

    public void setAiInsight(String aiInsight) {
        this.aiInsight = aiInsight != null ? aiInsight : "";
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction != null ? recommendedAction : "";
    }

    public String getAiReason() {
        return aiReason;
    }

    public void setAiReason(String aiReason) {
        this.aiReason = aiReason != null ? aiReason : "";
    }

    public String getCrmNote() {
        return crmNote;
    }

    public void setCrmNote(String crmNote) {
        this.crmNote = crmNote != null ? crmNote : "";
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource != null ? dataSource : "";
    }

    public String getPossessionTime() {
        return possessionTime;
    }

    public void setPossessionTime(String possessionTime) {
        this.possessionTime = possessionTime != null ? possessionTime : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
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
