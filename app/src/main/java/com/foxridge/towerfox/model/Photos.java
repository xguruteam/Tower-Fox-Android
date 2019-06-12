package com.foxridge.towerfox.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName="Photos")
public class Photos {
    @PrimaryKey
    @NonNull
    private String AdhocPhotoID;
    private String ProjectPhotoID = "";
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
    private Boolean RequireSectorPosition = false;
    private String ParentCategoryID = "";
    private String Latitude = "";
    private String Longitude = "";
    @ColumnInfo(name="IsAdhoc")
    private Boolean isAdhoc = false;
    private Integer SortOrder = 0;
    @Ignore
    @ColumnInfo(name="Quantity")
    private Integer Quantity = 0;


    @Override
    public String toString() {
        return "AdhocPhotoID: " + AdhocPhotoID + "\n" +
                "ProjectPhotoID: " + ProjectPhotoID + "\n" +
                "ProjectID: " + ProjectID + "\n" +
                "CategoryID: " + CategoryID + "\n" +
                "ItemID: " + ItemID + "\n" +
                "SectorID: " + SectorID + "\n" +
                "PositionID: " + PositionID + "\n" +
                "ItemName: " + ItemName + "\n" +
                "Description: " + Description + "\n" +
                "Comments: " + Comments + "\n" +
                "TakenBy: " + TakenBy + "\n" +
                "TakenDate: " + TakenDate + "\n" +
                "Status: " + Status + "\n" +
                "ReferenceImageName: " + ReferenceImageName + "\n" +
                "CapturedImageName: " + CapturedImageName + "\n" +
                "RequireSectorPosition: " + RequireSectorPosition + "\n" +
                "ParentCategoryID: " + ParentCategoryID + "\n" +
                "Latitude: " + Latitude + "\n" +
                "Longitude: " + Longitude + "\n" +
                "SortOrder: " + SortOrder;
    }

    @NonNull
    public String getAdhocPhotoID() {
        return AdhocPhotoID;
    }

    public void setAdhocPhotoID(@NonNull String adhocPhotoID) {
        AdhocPhotoID = adhocPhotoID;
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

    public void setProjectID(String projectID) {
        ProjectID = projectID;
    }

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

    public Boolean getRequireSectorPosition() {
        return RequireSectorPosition;
    }

    public void setRequireSectorPosition(Boolean requireSectorPosition) {
        RequireSectorPosition = requireSectorPosition;
    }

    public String getParentCategoryID() {
        return ParentCategoryID;
    }

    public void setParentCategoryID(String parentCategoryID) {
        ParentCategoryID = parentCategoryID;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public Integer getSortOrder() {
        return SortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        SortOrder = sortOrder;
    }

    public Boolean getAdhoc() {
        return isAdhoc;
    }

    public void setAdhoc(Boolean adhoc) {
        isAdhoc = adhoc;
    }

    public Integer getQuantity() {
        return Quantity;
    }

    public void setQuantity(Integer quantity) {
        Quantity = quantity;
    }
}
