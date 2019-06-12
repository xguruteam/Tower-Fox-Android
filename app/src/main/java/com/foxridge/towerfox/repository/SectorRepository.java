package com.foxridge.towerfox.repository;

import com.foxridge.towerfox.model.AppDatabase;
import com.foxridge.towerfox.model.Sector;

public class SectorRepository {
    private static final String TAG = SectorRepository.class.getName();
    private AppDatabase appDatabase;

    public SectorRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    private void insertProjects(Sector sector) {
        appDatabase.getSectorDao().inserSector(sector);
    }

    private void updateProjects(Sector sector) {
        appDatabase.getSectorDao().updateSector(sector);
    }

    public void clearAll() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.clearAllTables();
            }
        });
        thread.start();
    }

    public Sector getSector( String id) {
        Sector sector = appDatabase.getSectorDao().getSector(id);
        if (sector != null) {
            return sector;
        } else {
            return null;
        }
    }

    public void saveOrUpdateSector(final Sector sector) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Sector  p = getSector(sector.getSectorID());
                if (p != null) {
                    updateProjects(sector);
                } else {
                    insertProjects(sector);
                }
            }
        });
        thread.start();
    }

}
