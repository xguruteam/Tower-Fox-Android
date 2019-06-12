package com.foxridge.towerfox.repository;

import android.arch.lifecycle.MutableLiveData;

import com.foxridge.towerfox.model.AppDatabase;
import com.foxridge.towerfox.model.Position;

import java.util.List;

public class PositionRepository {
    private static final String TAG = PositionRepository.class.getName();

    private MutableLiveData<List<Position>> nearbyRetailersLiveData = new MutableLiveData<>();
    private AppDatabase appDatabase;
    public PositionRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    private void inserPosition(Position position) {
        appDatabase.getPhositionDao().inserPosition(position);
    }

    private void updatePosition(Position position) {
        appDatabase.getPhositionDao().updatePosition(position);
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

    public Position getPosition( String id) {
        Position position = appDatabase.getPhositionDao().getPosition(id);
        if (position != null) {
            return position;
        } else {
            return null;
        }
    }

    public void saveOrUpdatePosition(final Position position) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Position  p = getPosition(position.getPositionID());
                if (p != null) {
                    updatePosition(position);
                } else {
                    inserPosition(position);
                }
            }
        });
        thread.start();
    }


}
