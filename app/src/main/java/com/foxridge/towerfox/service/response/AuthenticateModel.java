package com.foxridge.towerfox.service.response;

import com.foxridge.towerfox.utils.Globals;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthenticateModel implements Serializable{
    @SerializedName("Status")
    private String status;
    private String AbsoluteExpiration = "";
    private String AppUserID = "";
    @SerializedName("Message")
    private String message = "";
    private String SlidingExpiration = "";
    private String VendorContactID = "";
    private String VendorID = "";
    @SerializedName("Token")
    private String token = "";


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void save() {
        Globals.getInstance().storage_saveObject("Token", token);
        Globals.getInstance().storage_saveObject("VendorContactID", VendorContactID);
        Globals.getInstance().storage_saveObject("VendorID", VendorID);
        Globals.getInstance().storage_saveObject("AbsoluteExpiration", AbsoluteExpiration);
        Globals.getInstance().storage_saveObject("SlidingExpiration", SlidingExpiration);
        Globals.getInstance().storage_saveObject("AppUserID", AppUserID);
    }

    public String getAbsoluteExpiration() {
        return AbsoluteExpiration;
    }

    public void setAbsoluteExpiration(String absoluteExpiration) {
        AbsoluteExpiration = absoluteExpiration;
    }

    public String getAppUserID() {
        return AppUserID;
    }

    public void setAppUserID(String appUserID) {
        AppUserID = appUserID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSlidingExpiration() {
        return SlidingExpiration;
    }

    public void setSlidingExpiration(String slidingExpiration) {
        SlidingExpiration = slidingExpiration;
    }

    public String getVendorContactID() {
        return VendorContactID;
    }

    public void setVendorContactID(String vendorContactID) {
        VendorContactID = vendorContactID;
    }

    public String getVendorID() {
        return VendorID;
    }

    public void setVendorID(String vendorID) {
        VendorID = vendorID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
