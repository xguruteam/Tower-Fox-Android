package com.foxridge.towerfox.model;

public class CategoryHeaderModel {
    private Integer Total;
    private Integer Required;
    private Integer Taken;
    private Integer Approved;
    private Integer Rejected;

    public Integer getTotal() {
        return Total;
    }

    public void setTotal(Integer total) {
        Total = total;
    }

    public Integer getRequired() {
        return Required;
    }

    public void setRequired(Integer required) {
        Required = required;
    }

    public Integer getTaken() {
        return Taken;
    }

    public void setTaken(Integer taken) {
        Taken = taken;
    }

    public Integer getApproved() {
        return Approved;
    }

    public void setApproved(Integer approved) {
        Approved = approved;
    }

    public Integer getRejected() {
        return Rejected;
    }

    public void setRejected(Integer rejected) {
        Rejected = rejected;
    }
}
