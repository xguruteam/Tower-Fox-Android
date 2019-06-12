package com.foxridge.towerfox;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;

import com.foxridge.towerfox.model.AppDatabase;
import com.foxridge.towerfox.repository.CategoriesRepository;
import com.foxridge.towerfox.repository.PhotosRepository;
import com.foxridge.towerfox.repository.PositionRepository;
import com.foxridge.towerfox.repository.ProjectsRepository;
import com.foxridge.towerfox.repository.SectorRepository;
import com.foxridge.towerfox.utils.Globals;

public class App extends Application {

    private static App app;
    private MutableLiveData<Boolean> closeLoginActivity = new MutableLiveData<>();
    public static boolean mPrefetchImages;
    private ProjectsRepository projectsRepository;
    private CategoriesRepository categoriesRepository;
    private SectorRepository sectorRepository;
    private PhotosRepository photosRepository;
    private PositionRepository positionRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Globals.getInstance().init(getApplicationContext());
        AppDatabase database = Room.databaseBuilder(this, AppDatabase.class, "towerfoxdb").build();

        projectsRepository = new ProjectsRepository(database);
        categoriesRepository = new CategoriesRepository(database);
        sectorRepository = new SectorRepository(database);
        photosRepository = new PhotosRepository(database);
        positionRepository = new PositionRepository(database);

    }

    public static App getApp() {
        return app;
    }

    public MutableLiveData<Boolean> getCloseLoginActivity() {
        return closeLoginActivity;
    }

    public ProjectsRepository getProjectsRepository() {
        return projectsRepository;
    }

    public void setProjectsRepository(ProjectsRepository projectsRepository) {
        this.projectsRepository = projectsRepository;
    }

    public CategoriesRepository getCategoriesRepository() {
        return categoriesRepository;
    }

    public void setCategoriesRepository(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    public SectorRepository getSectorRepository() {
        return sectorRepository;
    }

    public void setSectorRepository(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    public PhotosRepository getPhotosRepository() {
        return photosRepository;
    }

    public void setPhotosRepository(PhotosRepository photosRepository) {
        this.photosRepository = photosRepository;
    }

    public PositionRepository getPositionRepository() {
        return positionRepository;
    }

    public void setPositionRepository(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }
}
