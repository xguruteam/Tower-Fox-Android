package com.foxridge.towerfox.repository;

import com.foxridge.towerfox.model.AppDatabase;
import com.foxridge.towerfox.model.CategoryDisplayModel;
import com.foxridge.towerfox.model.CategoryHeaderModel;
import com.foxridge.towerfox.model.ItemCountModel;
import com.foxridge.towerfox.model.LocationMatrixModel;
import com.foxridge.towerfox.model.NextItemModel;
import com.foxridge.towerfox.model.PhotoRemainingModel;
import com.foxridge.towerfox.model.Photos;
import com.foxridge.towerfox.model.ProjectDisplayModel;
import com.foxridge.towerfox.model.Projects;
import com.foxridge.towerfox.model.RejectDisplayModel;

import java.util.List;

public class ProjectsRepository {

    private static final String TAG = ProjectsRepository.class.getName();

    private AppDatabase appDatabase;

    public ProjectsRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    private void insertProjects(Projects projects) {
        appDatabase.getProjectDao().insertProjects(projects);
    }

    private void updateProjects(Projects projects) {
        appDatabase.getProjectDao().updateProjects(projects);
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

    public Projects getProjects( String id) {
        Projects projects = appDatabase.getProjectDao().getProjectByID(id);
        if (projects != null) {
            return projects;
        } else {
            return null;
        }
    }

    public Integer getProjectsCount() {
        return appDatabase.getProjectDao().getProjectsCount();
    }

    public void saveOrUpdateProject(final Projects projects) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Projects  p = getProjects(projects.getProjectID());
                if (p != null) {
                    updateProjects(projects);
                } else {
                    insertProjects(projects);
                }
            }
        });
        thread.start();
    }

    public List<ProjectDisplayModel> getDisplayProject(){
        return appDatabase.getAppDatabaseDao().displayProject();
    }

    public List<RejectDisplayModel> getDisplayReject(){
        return appDatabase.getAppDatabaseDao().displayReject();
    }

    public List<Photos> getCapturedImages(){
        return appDatabase.getAppDatabaseDao().getCapturedImages();
    }

    public List<PhotoRemainingModel> getPhotoRemainingCount(String projectID){
        return appDatabase.getAppDatabaseDao().getRemainingCount(projectID);
    }

    public List<Photos> getUpdatePhotos(){
        return appDatabase.getAppDatabaseDao().getUpdatePhotos();
    }

    public void updatePhotos1(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.getAppDatabaseDao().updatePhotos1();
            }
        });
        thread.start();
    }

    public void updatePhotos2(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.getAppDatabaseDao().updatePhotos2();
            }
        });
        thread.start();
    }

    public List<CategoryHeaderModel> getCategoryHeader1(String projectID, String parentID){
        String likes = String.format("%%,%s,%%", parentID);
        return appDatabase.getAppDatabaseDao().getCategoryHeaderCount1(projectID, likes);
    }

    public List<CategoryHeaderModel> getCategoryHeader2(String projectID, String parentID, String sectorID){
        String likes = String.format("%%,%s,%%", parentID);
        return appDatabase.getAppDatabaseDao().getCategoryHeaderCount2(projectID, sectorID, likes);
    }

    public List<CategoryHeaderModel> getCategoryHeader3(String projectID, String parentID, String sectorID, String positionID){
        String likes = String.format("%%,%s,%%", parentID);
        return appDatabase.getAppDatabaseDao().getCategoryHeaderCount3(projectID, positionID, sectorID, likes);
    }

    public List<CategoryDisplayModel> getCategoryDisplay1(String projectID, String parentID){
        return appDatabase.getAppDatabaseDao().getCategoryDisplay1(projectID, parentID);
    }

    public List<CategoryDisplayModel> getCategoryDisplay2(String projectID, String parentID){
        return appDatabase.getAppDatabaseDao().getCategoryDisplay2(projectID, parentID);
    }

    public List<CategoryDisplayModel> getCategoryDisplay3(String projectID, String parentID, String sectorID){
        return appDatabase.getAppDatabaseDao().getCategoryDisplay3(projectID, parentID, sectorID);
    }

    public List<CategoryDisplayModel> getCategoryDisplay4(String projectID, String parentID, String sectorID, String positionID){
        return appDatabase.getAppDatabaseDao().getCategoryDisplay4(projectID, parentID, sectorID, positionID);
    }

    public List<CategoryHeaderModel> getCategoryDisplayCount1(String projectID, String parentID, String catID){
        return appDatabase.getAppDatabaseDao().getCategoryCount1(projectID, parentID, catID);
    }

    public List<CategoryHeaderModel> getCategoryDisplayCount2(String projectID, String parentID, String sectorID, String catID){
        return appDatabase.getAppDatabaseDao().getCategoryCount2(projectID, parentID, sectorID, catID);
    }

    public List<CategoryHeaderModel> getCategoryDisplayCount3(String projectID, String catID){
        String likes = String.format("%%,%s,%%", catID);
        return appDatabase.getAppDatabaseDao().getCategoryCount3(projectID, likes);
    }

    public List<ItemCountModel> getItemCountByCategory1(String projectID, String parentID, String sectorID, String positionID){
        return appDatabase.getAppDatabaseDao().getItemsCountInSelectedCategory1(projectID, parentID, sectorID, positionID);
    }

    public List<ItemCountModel> getItemCountByCategory2(String projectID, String parentID, String sectorID, String positionID){
        return appDatabase.getAppDatabaseDao().getItemsCountInSelectedCategory2(projectID, parentID, sectorID, positionID);
    }

    public List<Photos> getPhotoDetail(String adhocPhotoID){
        return appDatabase.getAppDatabaseDao().getPhotoDetail(adhocPhotoID);
    }

    public void deleteProject(final String id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.getProjectDao().deleteProject(id);
                appDatabase.getCategoriesDao().deleteCategory(id);
                appDatabase.getPhotosDao().deletePhotosByProjectID(id);
            }
        }).start();
    }

    public void insertAdhocPhotoDataDB(final Photos photos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.getPhotosDao().insertPhoto(photos);
            }
        }).start();
    }
    public void resetPhoto(final String userName, final String takenDate, final String adhocPhotoID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.getAppDatabaseDao().resetPhoto(userName, takenDate, adhocPhotoID);
            }
        }).start();
    }
    public void updateCapturedImage(final Photos photos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.getAppDatabaseDao().updateCapturedPhoto(photos.getCapturedImageName(), photos.getLatitude(), photos.getLongitude(), photos.getTakenBy(), photos.getTakenDate(), photos.getSectorID(), photos.getPositionID(), photos.getAdhocPhotoID());
            }
        }).start();
    }
    public List<NextItemModel> getNextItemToDisplay1(final String projectID, final String parentID, final String sectorID, final String positionID) {
        return  appDatabase.getAppDatabaseDao().getNextItemToDisplay1(projectID, parentID, sectorID,positionID);
    }
    public List<NextItemModel> getNextItemToDisplay2(final String projectID, final String parentID, final String sectorID, final String positionID) {
        return  appDatabase.getAppDatabaseDao().getNextItemToDisplay2(projectID, parentID, sectorID,positionID);
    }
    public List<LocationMatrixModel> getLocationMatrix(String adhocPhotoID, String cateoryIDList) {
        return appDatabase.getAppDatabaseDao().getLocationMatrix(adhocPhotoID, cateoryIDList);
    }
}
