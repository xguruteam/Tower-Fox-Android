package com.foxridge.towerfox.model;

public class RejectDisplayModel {
    private String Sample = "";
    private String CategoryName = "";
    private String ProjectPhotoID = "";
    private String ProjectName = "";
    private String ProjectID = "";
    private String CategoryID = "";
    private String ItemID = "";
    private String SectorID = "";
    private String PositionID = "";
    private String ItemName = "";
    private String Description = "";
    private String Comments = "";
    private String TakenBy = "";
    private String TakenDate = "";
    private Integer Status = 0;
    private String ReferenceImageName = "";
    private String CapturedImageName = "";
    private Integer Quantity = 0;
    private boolean RequireSectorPosition = false;
    private String ParentCategoryID = "";
    private Double Latitude = 0.0;
    private Double Longitude = 0.0;
    private Integer SortOrder = 0;
    private String AdhocPhotoID = "";

    public String getSample() {
        return Sample;
    }

    public void setSample(String sample) {
        Sample = sample;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getProjectPhotoID() {
        return ProjectPhotoID;
    }

    public void setProjectPhotoID(String projectPhotoID) {
        ProjectPhotoID = projectPhotoID;
    }

    public String getProjectID() {
        return ProjectID;
    }

    public String getProjectName() { return ProjectName; }

    public void setProjectID(String projectID) {
        ProjectID = projectID;
    }

    public void setProjectName(String projectName) { ProjectName = projectName; }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
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

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getTakenBy() {
        return TakenBy;
    }

    public void setTakenBy(String takenBy) {
        TakenBy = takenBy;
    }

    public String getTakenDate() {
        return TakenDate;
    }

    public void setTakenDate(String takenDate) {
        TakenDate = takenDate;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getReferenceImageName() {
        return ReferenceImageName;
    }

    public void setReferenceImageName(String referenceImageName) {
        ReferenceImageName = referenceImageName;
    }

    public String getCapturedImageName() {
        return CapturedImageName;
    }

    public void setCapturedImageName(String capturedImageName) {
        CapturedImageName = capturedImageName;
    }

    public Integer getQuantity() {
        return Quantity;
    }

    public void setQuantity(Integer quantity) {
        Quantity = quantity;
    }

    public boolean isRequireSectorPosition() {
        return RequireSectorPosition;
    }

    public void setRequireSectorPosition(boolean requireSectorPosition) {
        RequireSectorPosition = requireSectorPosition;
    }

    public String getParentCategoryID() {
        return ParentCategoryID;
    }

    public void setParentCategoryID(String parentCategoryID) {
        ParentCategoryID = parentCategoryID;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }


    public Integer getSortOrder() {
        return SortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        SortOrder = sortOrder;
    }

    public String getAdhocPhotoID() {
        return AdhocPhotoID;
    }

    public void setAdhocPhotoID(String adhocPhotoID) {
        AdhocPhotoID = adhocPhotoID;
    }

}
