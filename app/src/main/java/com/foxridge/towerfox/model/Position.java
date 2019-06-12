package com.foxridge.towerfox.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName="Position")
public class Position {
    @PrimaryKey
    @NonNull
    private String PositionID;
    private String Name;

    @Override
    public String toString() {
        return "PositionID: " + PositionID + "\n" +
                "PositionName: " + Name;
    }

    @NonNull
    public String getPositionID() {
        return PositionID;
    }

    public void setPositionID(@NonNull String positionID) {
        PositionID = positionID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
