package com.foxridge.towerfox.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.foxridge.towerfox.model.Projects;

import java.util.List;

@Dao
public interface ProjectsDao {

    @Insert
    void insertProjects(Projects projects);

    @Update
    void updateProjects(Projects projects);

    @Delete
    void deleteProjects(Projects projects);

    @Query("select * from Projects")
    List<Projects> getAll();

    @Query("select count(*) from Projects")
    Integer getProjectsCount();

    @Query("select * from Projects where ProjectID=:projectID")
    Projects getProjectByID(String projectID);

    @Query("select * from Projects")
    LiveData<List<Projects>> getAllLive();

    @Query("delete from Projects where ProjectID = :id")
    void deleteProject(String id);


}
