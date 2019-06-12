package com.foxridge.towerfox.repository;

import com.foxridge.towerfox.model.AppDatabase;
import com.foxridge.towerfox.model.Photos;

public class PhotosRepository {
    private static final String TAG = PhotosRepository.class.getName();

    private AppDatabase appDatabase;
    public PhotosRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }
    private void insertPhotos(Photos photos) {
        appDatabase.getPhotosDao().insertPhoto(photos);
    }

    private void updatePhotos(Photos photos) {
        appDatabase.getPhotosDao().updatePhoto(photos);
    }

    public void clearAll() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.clearAllTables();
//                App.getApp().setCurrentUser(null);
//                App.getApp().getAppSettings().setAskAboutMedic(true);
//                App.getApp().getAppSettings().setAskScanDocs(true);
            }
        });
        thread.start();
    }

    public Photos getPhotos( String id) {
        Photos photos = appDatabase.getPhotosDao().getPhotos(id);
        if (photos != null) {
            return photos;
        } else {
            return null;
        }
    }
    public Photos getPhotos1( String id) {
        Photos photos = appDatabase.getPhotosDao().getPhotos1(id);
        if (photos != null) {
            return photos;
        } else {
            return null;
        }
    }

    public void saveOrUpdatePhotos(final Photos photos) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                appDatabase.getAppDatabaseDao().insertAndUpdatePhotos(photos.getProjectPhotoID(), photos.getProjectID(), photos.getCategoryID(), photos.getItemID(), photos.getSectorID(), photos.getPositionID(),
//                        photos.getItemName(), photos.getDescription(),photos.getComments(), photos.getTakenBy(), photos.getTakenDate(), photos.getStatus(), photos.getReferenceImageName(), photos.getCapturedImageName(),
//                        photos.getQuantity(), photos.getRequireSectorPosition(), photos.getCategoryID(), photos.getLatitude(), photos.getLongitude(), photos.getAdhoc(), photos.getAdhocPhotoID(), photos.getSortOrder());
                Photos  p = getPhotos(photos.getAdhocPhotoID());
                if (p != null) {
                    updatePhotos(photos);
                } else {
                    insertPhotos(photos);
                }
            }
        });
        thread.start();
    }

    public void deleteDeletedPhotos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.getPhotosDao().deleteDeletedPhoto();
            }
        }).start();
    }
}
