package com.foxridge.towerfox.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.foxridge.towerfox.model.NavigationStack;
import com.foxridge.towerfox.viewmodels.SyncViewModel;

import java.util.ArrayList;
import java.util.Date;

public class Globals {
    public String SERVICE_PROTOCOL = "https://";
    public String SERVICES_PATH = "/api12/CMPv2Services";

    public String policyBase64 = "eyJleHBpcmF0aW9uIjoiMjAyMC0xMi0zMVQxMjowMDowMC4wMDBaIiwiY29uZGl0aW9ucyI6W3siYnVja2V0IjoibGlmZWN5Y2xlLWNsb3Nlb3V0LWRlbW8tcGhvdG9zIn0sWyJzdGFydHMtd2l0aCIsIiRrZXkiLCIiXSx7ImFjbCI6InB1YmxpYy1yZWFkIn0sWyJzdGFydHMtd2l0aCIsIiRDb250ZW50LVR5cGUiLCIiXSxbImNvbnRlbnQtbGVuZ3RoLXJhbmdlIiwwLDUyNDI4ODAwMF1dfQ==";
    public String signature = "fgSHgALFA5vLeXzjoS12rdM6GFQ=";
    public String awsKey = "AKIAJIVNJLWFTIUBQ4PQ";
    public String acl = "public-read";

    public String projectID = "";
    public ArrayList<String> projectArray = new ArrayList<>();
    public ArrayList<String> categoryArray = new ArrayList<>();
    public ArrayList<String> photosArray = new ArrayList<>();

    public ArrayList<String> referenceImageNamesList = new ArrayList<>();
    public ArrayList<String> capturedImageNamesList = new ArrayList<>();
    public ArrayList<String> uploadImageNamesList = new ArrayList<>();
    public int uploadCount = 0;
    public int downloadCount = 0;
    public int downloadCapturedCount = 0;
    public boolean isResetPhotoAvailable = false;
    public int intTotalImagesCount = 0;
    public int intTotalImagesUploadedCount = 0;
//    private int s3Uploader: AWSS3TransferManager!
    public String s3URI = "https://lifecycle-closeout-demo-photos.s3.amazonaws.com/";

    public ArrayList<String> categoriesStack = new ArrayList<>();
    public ArrayList<String> categoriesListStringArray = new ArrayList<>();
    public ArrayList<String> categoriesListNameArray = new ArrayList<>();
    public ArrayList<String> projectsGlobalArray = new ArrayList<>();
    public boolean isFromBagroundSync = true;
    public boolean isFromProjects = true;
    public boolean isAdhocPhoto = false;
    public String _fileSystem = "";
    public String _loggingFileSystem = "";
    public boolean FILE_LOGGING_ENABLED = true;
    public boolean LOGGING_ENABLED = false;
    public String pushNotification = "";
    public boolean isFromPushNotification = false;
    public boolean isPushNotificationSilentSync = false;
    public boolean isRLList = false;
    public ArrayList<NavigationStack> navigationStack = new ArrayList<>();

    private static Globals instance;
    private Context context;
    // Global variable
    public SharedPreferences sharedPreferences ;
    public SharedPreferences.Editor editor;

    public String IMAGE_LOCATION_PATH = "";

    public SyncViewModel syncViewModel;

    private Globals(){
    }
    public Globals(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public void init(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        IMAGE_LOCATION_PATH = context.getExternalCacheDir().getPath();
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }

    public void storage_saveObject(String key, String object) {
        editor.putString(key, object);
        editor.apply();
    }

    public void storage_saveObject(String key, int object) {
        editor.putInt(key, object);
        editor.apply();
    }

    public void storage_saveObject(String key, float object) {
        editor.putFloat(key, object);
        editor.apply();
    }

    public void storage_saveObject(String key, Double object) {
        editor.putFloat(key, object.floatValue());
        editor.apply();
    }

    public void storage_saveObject(String key, boolean object) {
        editor.putBoolean(key, object);
        editor.apply();
    }

    public String storage_loadString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public int storage_loadInteger(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public float storage_loadFloat(String key) {
        return sharedPreferences.getFloat(key, 0f);
    }

    public boolean storage_loadBool(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void storage_removeItem(String key) {
        if (sharedPreferences.contains(key)) {
            editor.remove(key).apply();
        }
    }

    public String getApplicationURL() {
        return SERVICE_PROTOCOL + storage_loadString("SERVER_IP") + SERVICES_PATH + "/Service1.svc/json/";
    }

    public String getServiceConnectivityURL(String serverIP)
    {
        return SERVICE_PROTOCOL + serverIP + SERVICES_PATH + "/Service1.svc/json/";
    }

    public String getDeviceInfoURL()
    {
        return getApplicationURL() + "SaveDeviceInfo";
    }

    public String getProjectsByProjectIDURL()
    {
        return getApplicationURL() + "GetProjectsbyID";
    }

    public String getCategorybyprjtIDURL(String _projectID)
    {
        return getApplicationURL() + "GetProjectCategorybyprjtID/" + _projectID;
    }

    public String GetPhotosByprjtIDURL(String _projectID)
    {
        return getApplicationURL() + "GetProjectPhotosByprjtID/" + _projectID;
    }

    public String getSectorsURL()
    {
        return getApplicationURL() + "GetSectors";
    }

    public String getPositionsURL()
    {
        return getApplicationURL() + "GetSectorPosition";
    }

    public String UpdateProjectPhotosURL()
    {
        return getApplicationURL() + "UpdateProjectPhotos";
    }
    public String getProjectPhotosURL()
    {
        return getApplicationURL() + "GetProjectPhotosByProjectsSyncInfo";
    }
    public String getReferencePhotosURL()
    {
        return getApplicationURL() + "GetReferencePhotosByProjectsSyncInfo";
    }

    public String getCapturedPhotosURL()
    {
        return getApplicationURL() + "GetCapturedPhotosByProjectsSyncInfo";
    }
    public String getValidateUserURL()
    {
        return getApplicationURL() + "AuthenticateNSiteUser";
    }
    public String getLogoutDeviceInfoURL()
    {
        return getApplicationURL() + "DeleteDeviceInfoLogOut";
    }
    public String getDeleteProjectURL()
    {
        return getApplicationURL() + "DeleteDeviceInfoByProject";
    }

    public String convertToJSONDate()
    {
        Date myDate = new Date(); // Your timezone!
        long myEpoch = myDate.getTime();
        return "/Date(" + myEpoch + ")/";
    }
}