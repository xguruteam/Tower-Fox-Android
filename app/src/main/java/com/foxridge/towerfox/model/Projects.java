package com.foxridge.towerfox.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName="Projects")
public class Projects {
    @PrimaryKey
    @NonNull
    private String ProjectID;
    private String ProjectName = "";
    private String FirstName = "";
    private String LastName = "";
    private String Description = "";
    private String CasperID = "";
    private String PaceID = "";

    @Override
    public String toString() {
        return "ProjectID: " + ProjectID + "\n" +
                "ProjectName: " + ProjectName + "\n" +
                "FirstName: " + FirstName + "\n" +
                "LastName: " + LastName + "\n" +
                "Description: " + Description + "\n" +
                "CasperID: " + CasperID + "\n" +
                "PaceID: " + PaceID;
    }

    @NonNull
    public String getProjectID() {
        return ProjectID;
    }

    public void setProjectID(@NonNull String projectID) {
        ProjectID = projectID;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCasperID() {
        return CasperID;
    }

    public void setCasperID(String casperID) {
        CasperID = casperID;
    }

    public String getPaceID() {
        return PaceID;
    }

    public void setPaceID(String paceID) {
        PaceID = paceID;
    }
}
