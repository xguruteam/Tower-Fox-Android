package com.foxridge.towerfox.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.foxridge.towerfox.model.dao.CategoriesDao;
import com.foxridge.towerfox.model.dao.PhotosDao;
import com.foxridge.towerfox.model.dao.PositionDao;
import com.foxridge.towerfox.model.dao.ProjectsDao;
import com.foxridge.towerfox.model.dao.SectorDao;

@Database(entities = {
        Projects.class,
        Categories.class,
        Photos.class,
        Sector.class,
        Position.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProjectsDao getProjectDao();
    public abstract CategoriesDao getCategoriesDao();
    public abstract PhotosDao getPhotosDao();
    public abstract SectorDao getSectorDao();
    public abstract PositionDao getPhositionDao();
    public abstract AppDatabaseDao getAppDatabaseDao();
}
