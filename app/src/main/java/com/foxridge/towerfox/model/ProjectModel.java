package com.foxridge.towerfox.model;

public class ProjectModel {
    private String Address = "";
    private String City = "";
    private String Description = "";
    private String FirstName = "";
    private String IsLocked = "";
    private String LastName = "";
    private String Latitude = "0";
    private String LockedBy = "";
    private String Longitude = "0";
    private String ProjectNumber = "";
    private String ProjectID = "";
    private String ProjectName = "";
    private String ServiceMessage = "";
    private String ServiceStatus = "";
    private String State = "";
    private Integer Status = 0;
    private String VenderID = "";
    private String casprid = "";
    private String faacode = "";

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getIsLocked() {
        return IsLocked;
    }

    public void setIsLocked(String isLocked) {
        IsLocked = isLocked;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLockedBy() {
        return LockedBy;
    }

    public void setLockedBy(String lockedBy) {
        LockedBy = lockedBy;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getProjectNumber() {
        return ProjectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        ProjectNumber = projectNumber;
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

    public String getServiceMessage() {
        return ServiceMessage;
    }

    public void setServiceMessage(String serviceMessage) {
        ServiceMessage = serviceMessage;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getVenderID() {
        return VenderID;
    }

    public void setVenderID(String venderID) {
        VenderID = venderID;
    }

    public String getFaacode() {
        return faacode;
    }

    public void setFaacode(String faacode) {
        this.faacode = faacode;
    }

    public String getCasprid() {
        return casprid;
    }

    public void setCasprid(String casprid) {
        this.casprid = casprid;
    }

    public String getServiceStatus() {
        return ServiceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        ServiceStatus = serviceStatus;
    }
}
