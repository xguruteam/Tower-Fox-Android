package com.foxridge.towerfox.model;

public class ItemCountModel {
    private Integer ItemsCount;
    private String ItemID;
    private String AdhocPhotoID;

    public Integer getItemsCount() {
        return ItemsCount;
    }

    public void setItemsCount(Integer itemsCount) {
        ItemsCount = itemsCount;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    public String getAdhocPhotoID() {
        return AdhocPhotoID;
    }

    public void setAdhocPhotoID(String adhocPhotoID) {
        AdhocPhotoID = adhocPhotoID;
    }
}
