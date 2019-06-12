package com.foxridge.towerfox.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.foxridge.towerfox.model.Photos;

import java.util.List;

@Dao
public interface PhotosDao {
    @Insert
    void insertPhoto(Photos photos);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePhoto(Photos photos);

    @Query("select * from Photos")
    LiveData<List<Photos>> getPhotosLiveData();

    @Query("select * from Photos")
    List<Photos> getPhotos();

    @Query("select * from Photos where ProjectPhotoID =:id")
    Photos getPhotos(String id);

    @Query("select * from Photos where AdhocPhotoID =:id")
    Photos getPhotos1(String id);

    @Query("delete from Photos where Status = 6")
    void deleteDeletedPhoto();

    @Query("delete from Photos where ProjectID = :id")
    void deletePhotosByProjectID(String id);

}
