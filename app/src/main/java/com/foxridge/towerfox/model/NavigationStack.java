package com.foxridge.towerfox.model;

public class NavigationStack {
    private String ParentID = "";
    private String SectorID = "";
    private String PositionID = "";
    private Boolean RequireSectorPosition = false;
    private String ProjectID = "";
    private String ProjectName = "";
    private String CategoryName = "";
    private String Type = "";
    private String ItemID = "";
    private Integer Required = 0;
    private Integer Taken = 0;
    private Integer Approved = 0;
    private Integer Rejected = 0;
    private Double TakenPercent = 0.0;
    private Double ApprovedPercent = 0.0;
    private Double RejectedPercent = 0.0;


    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

    public String getSectorID() {
        return SectorID;
    }

    public void setSectorID(String sectorID) {
        SectorID = sectorID;
    }

    public String getPositionID() {
        return PositionID;
    }

    public void setPositionID(String positionID) {
        PositionID = positionID;
    }

    public boolean getRequireSectorPosition() {
        return RequireSectorPosition;
    }

    public void setRequireSectorPosition(boolean requireSectorPosition) {
        RequireSectorPosition = requireSectorPosition;
    }

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

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
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

    public Double getTakenPercent() {
        return TakenPercent;
    }

    public void setTakenPercent(Double takenPercent) {
        TakenPercent = takenPercent;
    }

    public Double getApprovedPercent() {
        return ApprovedPercent;
    }

    public void setApprovedPercent(Double approvedPercent) {
        ApprovedPercent = approvedPercent;
    }

    public Double getRejectedPercent() {
        return RejectedPercent;
    }

    public void setRejectedPercent(Double rejectedPercent) {
        RejectedPercent = rejectedPercent;
    }
}
