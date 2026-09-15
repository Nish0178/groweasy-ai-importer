package com.groweasy.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CrmRecord {

    @JsonProperty("created_at")
    private String createdAt = "";

    @JsonProperty("name")
    private String name = "";

    @JsonProperty("email")
    private String email = "";

    @JsonProperty("country_code")
    private String countryCode = "";

    @JsonProperty("mobile_without_country_code")
    private String mobileWithoutCountryCode = "";

    @JsonProperty("company")
    private String company = "";

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

    @JsonProperty("crm_note")
    private String crmNote = "";

    @JsonProperty("data_source")
    private String dataSource = "";

    @JsonProperty("possession_time")
    private String possessionTime = "";

    @JsonProperty("description")
    private String description = "";

    public CrmRecord() {
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
}
