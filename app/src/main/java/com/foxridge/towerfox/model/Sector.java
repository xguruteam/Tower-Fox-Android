package com.foxridge.towerfox.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName="Sector")
public class Sector {
    @PrimaryKey
    @NonNull
    private String SectorID;

    private String Name;

    @Override
    public String toString() {
        return "SectorID: " + SectorID + "\n" +
                "SectorName: " + Name;
    }

    @NonNull
    public String getSectorID() {
        return SectorID;
    }

    public void setSectorID(@NonNull String sectorID) {
        SectorID = sectorID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
