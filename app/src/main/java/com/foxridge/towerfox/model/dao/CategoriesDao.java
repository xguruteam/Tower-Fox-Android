package com.foxridge.towerfox.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.foxridge.towerfox.model.Categories;

import java.util.List;

@Dao
public interface CategoriesDao {
    @Insert
    void insert(Categories categories);

    @Delete
    void delete(Categories categories);

    @Update
    void update(Categories categories);

    @Query("select * from Categories order by SortOrder desc")
    LiveData<List<Categories>> getAllCategoriesLiveData();

    @Query("select * from Categories where CategoryID=:id")
    Categories getCategory(String id);


    @Query("delete from Categories")
    void deleteAll();

    @Query("delete from Categories where ProjectID = :id")
    void deleteCategory(String id);


}
