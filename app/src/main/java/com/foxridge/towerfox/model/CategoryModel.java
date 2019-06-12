package com.foxridge.towerfox.model;

public class CategoryModel {
    private String CategoryID = "";
    private String CategoryName = "";
    private boolean ContainsSectorPosition = false;
    private String CretedBy = "";
    private String CreatedDate = "";
    private String Description = "";
    private String ModifiedBy = "";
    private String ModifiedDate = "";
    private String ParentCategoryID = "";
    private String ProjectID = "";
    private Integer sortOrder = 0;
    private boolean Status = false;

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public boolean isContainsSectorPosition() {
        return ContainsSectorPosition;
    }

    public void setContainsSectorPosition(boolean containsSectorPosition) {
        ContainsSectorPosition = containsSectorPosition;
    }

    public String getCretedBy() {
        return CretedBy;
    }

    public void setCretedBy(String cretedBy) {
        CretedBy = cretedBy;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getModifiedBy() {
        return ModifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        ModifiedBy = modifiedBy;
    }

    public String getModifiedDate() {
        return ModifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        ModifiedDate = modifiedDate;
    }

    public String getParentCategoryID() {
        return ParentCategoryID;
    }

    public void setParentCategoryID(String parentCategoryID) {
        ParentCategoryID = parentCategoryID;
    }

    public String getProjectID() {
        return ProjectID;
    }

    public void setProjectID(String projectID) {
        ProjectID = projectID;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }
}
