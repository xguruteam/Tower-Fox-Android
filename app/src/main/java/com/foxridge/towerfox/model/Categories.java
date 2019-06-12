package com.foxridge.towerfox.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName="Categories")
public class Categories {
    @NonNull
    private String ProjectID;
    @PrimaryKey
    @NonNull
    private String CategoryID = "";
    private String CategoryName = "";
    private String ParentCategoryID = "";
    private Boolean ContainsSectorPosition = false;
    private Integer SortOrder = 0;
    @Override
    public String toString() {
        return "ProjectID: " + ProjectID + "\n" +
                "CategoryID: " + CategoryID + "\n" +
                "CategoryName: " + CategoryName + "\n" +
                "ParentCategoryID: " + ParentCategoryID + "\n" +
                "ContainsSectorPosition: " + ContainsSectorPosition + "\n" +
                "SortOrder: " + SortOrder;
    }


    @NonNull
    public String getProjectID() {
        return ProjectID;
    }

    public void setProjectID(@NonNull String projectID) {
        ProjectID = projectID;
    }

    @NonNull
    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(@NonNull String categoryID) {
        CategoryID = categoryID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getParentCategoryID() {
        return ParentCategoryID;
    }

    public void setParentCategoryID(String parentCategoryID) {
        ParentCategoryID = parentCategoryID;
    }

    public Boolean getContainsSectorPosition() {
        return ContainsSectorPosition;
    }

    public void setContainsSectorPosition(Boolean containsSectorPosition) {
        ContainsSectorPosition = containsSectorPosition;
    }

    public Integer getSortOrder() {
        return SortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        SortOrder = sortOrder;
    }
}
