package com.foxridge.towerfox.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.foxridge.towerfox.model.Position;

import java.util.List;

@Dao
public interface PositionDao {
    @Insert
    void inserPosition(Position position);

    @Update
    void updatePosition(Position position);

    @Query("select * from Position")
    LiveData<List<Position>> getPositionLiveData();

    @Query("select * from Position where PositionID=:id")
    Position getPosition(String id);
}
