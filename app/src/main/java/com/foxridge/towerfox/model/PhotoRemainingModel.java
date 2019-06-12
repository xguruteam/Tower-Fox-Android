package com.foxridge.towerfox.model;

public class PhotoRemainingModel {
    private Integer Total;
    private Integer requiredCount;
    private Integer takenCount;
    private Integer ApprovedCount;
    private Integer rejectedCount;
    private Integer OutOfScopeCount;
    private Double Required;
    private Double Taken;
    private Double Approved;
    private Double Rejected;

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
