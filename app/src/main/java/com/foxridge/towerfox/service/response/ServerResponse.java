package com.foxridge.towerfox.service.response;

import com.foxridge.towerfox.utils.Globals;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ServerResponse implements Serializable{
    @SerializedName("Status")
    private String status;
    private String supportEmailAddress = "";
    private String supportPhone = "";
    private String supportWebAddress = "";
    private String targetMaxImageCaptureHeight = "";
    private String targetMaxImageCaptureWidth = "";
    private String useAWSImageUpload = "";


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSupportEmailAddress() {
        return supportEmailAddress;
    }

    public void setSupportEmailAddress(String supportEmailAddress) {
        this.supportEmailAddress = supportEmailAddress;
    }

    public String getSupportPhone() {
        return supportPhone;
    }

    public void setSupportPhone(String supportPhone) {
        this.supportPhone = supportPhone;
    }

    public String getSupportWebAddress() {
        return supportWebAddress;
    }

    public void setSupportWebAddress(String supportWebAddress) {
        this.supportWebAddress = supportWebAddress;
    }

    public String getTargetMaxImageCaptureHeight() {
        return targetMaxImageCaptureHeight;
    }

    public void setTargetMaxImageCaptureHeight(String targetMaxImageCaptureHeight) {
        this.targetMaxImageCaptureHeight = targetMaxImageCaptureHeight;
    }

    public String getTargetMaxImageCaptureWidth() {
        return targetMaxImageCaptureWidth;
    }

    public void setTargetMaxImageCaptureWidth(String targetMaxImageCaptureWidth) {
        this.targetMaxImageCaptureWidth = targetMaxImageCaptureWidth;
    }

    public String getUseAwsImageUpload() {
        return useAWSImageUpload;
    }

    public void setUseAWSImageUpload(String useAWSImageUpload) {
        this.useAWSImageUpload = useAWSImageUpload;
    }

    public void save() {
        Globals.getInstance().storage_saveObject("supportEmailAddress", supportEmailAddress);
        Globals.getInstance().storage_saveObject("supportWebAddress", supportWebAddress);
        Globals.getInstance().storage_saveObject("supportPhone", supportPhone);
        Globals.getInstance().storage_saveObject("targetMaxImageCaptureHeight", targetMaxImageCaptureHeight);
        Globals.getInstance().storage_saveObject("targetMaxImageCaptureWidth", targetMaxImageCaptureWidth);
        Globals.getInstance().storage_saveObject("useAWSImageUpload", useAWSImageUpload);
    }
}
