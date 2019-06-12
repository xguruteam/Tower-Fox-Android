package com.foxridge.towerfox.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.foxridge.towerfox.model.Sector;

import java.util.List;

@Dao
public interface SectorDao {
    @Insert
    void inserSector(Sector sector);

    @Update
    void updateSector(Sector sector);

    @Query("select * from Sector")
    LiveData<List<Sector>> getSectorLiveData();

    @Query("select * from Sector where SectorID=:id")
    Sector getSector(String id);
}
