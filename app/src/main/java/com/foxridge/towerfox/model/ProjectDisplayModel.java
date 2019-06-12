package com.foxridge.towerfox.model;

public class ProjectDisplayModel {
    private String ProjectID = "";
    private String ProjectName = "";
    private String CasperID = "";
    private String PaceID = "";
    private String Description = "";
    private String FirstName = "";
    private String LastName = "";
    private Integer Total = 0;
    private Integer requiredCount = 0;
    private Integer takenCount = 0;
    private Integer ApprovedCount = 0;
    private Integer rejectedCount = 0;
    private Integer OutOfScopeCount = 0;
    private Double Required = 0.0;
    private Double Taken = 0.0;
    private Double Approved = 0.0;
    private Double Rejected = 0.0;


    public String getProjectID() {
        return ProjectID;
    }

    public void setProjectID(String projectID) {
        ProjectID = projectID;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public String getCasperID() {
        return CasperID;
    }

    public void setCasperID(String casperID) {
        CasperID = casperID;
    }

    public String getPaceID() {
        return PaceID;
    }

    public void setPaceID(String paceID) {
        PaceID = paceID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public Integer getTotal() {
        return Total;
    }

    public void setTotal(Integer total) {
        Total = total;
    }

    public Integer getRequiredCount() {
        return requiredCount;
    }

    public void setRequiredCount(Integer requiredCount) {
        this.requiredCount = requiredCount;
    }

    public Integer getTakenCount() {
        return takenCount;
    }

    public void setTakenCount(Integer takenCount) {
        this.takenCount = takenCount;
    }

    public Integer getApprovedCount() {
        return ApprovedCount;
    }

    public void setApprovedCount(Integer approvedCount) {
        ApprovedCount = approvedCount;
    }

    public Integer getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(Integer rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public Integer getOutOfScopeCount() {
        return OutOfScopeCount;
    }

    public void setOutOfScopeCount(Integer outOfScopeCount) {
        OutOfScopeCount = outOfScopeCount;
    }

    public Double getRequired() {
        return Required;
    }

    public void setRequired(Double required) {
        Required = required;
    }

    public Double getTaken() {
        return Taken;
    }

    public void setTaken(Double taken) {
        Taken = taken;
    }

    public Double getApproved() {
        return Approved;
    }

    public void setApproved(Double approved) {
        Approved = approved;
    }

    public Double getRejected() {
        return Rejected;
    }

    public void setRejected(Double rejected) {
        Rejected = rejected;
    }
}
