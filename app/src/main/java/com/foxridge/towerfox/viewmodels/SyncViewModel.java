package com.foxridge.towerfox.viewmodels;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.foxridge.towerfox.App;
import com.foxridge.towerfox.model.Categories;
import com.foxridge.towerfox.model.CategoryDisplayModel;
import com.foxridge.towerfox.model.CategoryHeaderModel;
import com.foxridge.towerfox.model.CategoryModel;
import com.foxridge.towerfox.model.EventPush;
import com.foxridge.towerfox.model.ItemCountModel;
import com.foxridge.towerfox.model.LocationMatrixModel;
import com.foxridge.towerfox.model.NavigationStack;
import com.foxridge.towerfox.model.NextItemModel;
import com.foxridge.towerfox.model.PhotoRemainingModel;
import com.foxridge.towerfox.model.PhotoUrlModel;
import com.foxridge.towerfox.model.Photos;
import com.foxridge.towerfox.model.Position;
import com.foxridge.towerfox.model.ProjectDisplayModel;
import com.foxridge.towerfox.model.ProjectModel;
import com.foxridge.towerfox.model.ProjectPhotosModel;
import com.foxridge.towerfox.model.Projects;
import com.foxridge.towerfox.model.RejectDisplayModel;
import com.foxridge.towerfox.model.Sector;
import com.foxridge.towerfox.service.AmazonService;
import com.foxridge.towerfox.service.RestService;
import com.foxridge.towerfox.service.response.BaseResponse;
import com.foxridge.towerfox.service.response.ServerResponse;
import com.foxridge.towerfox.utils.Globals;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SyncViewModel extends ViewModel {
    private String version = "";
    private String deviceVersion = "";
    private String model = "";
    private int intTotalImagesCount = 0;
    private int intTotalUploadImageCount = 0;


    private static final String TAG = SyncViewModel.class.getName();
    private MutableLiveData<String> jsonDevicePostLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> jsonDeleteProject = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> getProjectArrayData = new MutableLiveData<>();

    private MutableLiveData<List<ProjectDisplayModel>> projectDisplayModels = new MutableLiveData<>();
    private MutableLiveData<List<RejectDisplayModel>> rejectDisplayModels = new MutableLiveData<>();
    public MutableLiveData<Integer> downloadReferencePhotos = new MutableLiveData<>();
    public MutableLiveData<Integer> downloadCapturedPhotos = new MutableLiveData<>();
    private MutableLiveData<Integer> uploadCapturedPhotos = new MutableLiveData<>();
    private MutableLiveData<Boolean> uploadError = new MutableLiveData<>();

    private MutableLiveData<List<CategoryDisplayModel>> categoryDisplayModels = new MutableLiveData<>();
    private MutableLiveData<List<PhotoRemainingModel>> photoRemainingModels = new MutableLiveData<>();
    private MutableLiveData<List<CategoryHeaderModel>> categoryDisplayHeaderModels = new MutableLiveData<>();
    private MutableLiveData<List<Photos>> photoDetail = new MutableLiveData<>();
    private MutableLiveData<Boolean> getNextItem = new MutableLiveData<>();
    private MutableLiveData<Boolean> showProgress = new MutableLiveData<>();
    private MutableLiveData<String> getLocationMatrix = new MutableLiveData<>();

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void setErrorLiveData(MutableLiveData<String> errorLiveData) {
        this.errorLiveData = errorLiveData;
    }

    public MutableLiveData<String> getJsonDevicePostLiveData() {
        return jsonDevicePostLiveData;
    }

    public void setJsonDevicePostLiveData(MutableLiveData<String> jsonDevicePostLiveData) {
        this.jsonDevicePostLiveData = jsonDevicePostLiveData;
    }

    public MutableLiveData<List<Photos>> getPhotoDetail() {
        return photoDetail;
    }

    public void setPhotoDetail(MutableLiveData<List<Photos>> photoDetail) {
        this.photoDetail = photoDetail;
    }

    private MutableLiveData<Boolean> serverConnectivity = new MutableLiveData<>();

    public MutableLiveData<Boolean> getServerConnectivity() {
        return serverConnectivity;
    }

    public void setServerConnectivity(MutableLiveData<Boolean> serverConnectivity) {
        this.serverConnectivity = serverConnectivity;
    }

    public void checkServerConnectivtiyTest(String _serverIP) {
        if (!Globals.getInstance().storage_loadString("SYNC").equals("")) {
            Globals.getInstance().storage_saveObject("SYNC", "");
            return;
        }
        showProgress.postValue(true);
        Globals.getInstance().storage_saveObject("SYNC", "true");
        Call<ServerResponse> call = RestService.getTestApi(_serverIP).serverConnectivityTest();
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Server !");
                    response.body().save();
                    Globals.getInstance().storage_saveObject("SYNC", "");
                    uploadDataToServer();
                } else {
                    Globals.getInstance().storage_saveObject("SYNC", "");
                    showProgress.postValue(false);
                    try {
                        String error = response.errorBody().string();
                        Log.e(TAG, "server IP: " + error);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    serverConnectivity.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                Globals.getInstance().storage_saveObject("SYNC", "");
                serverConnectivity.postValue(false);
                showProgress.postValue(false);
            }
        });
    }

    public void sendDeviceInfo(Context context) {
        if (checkConnection()) {
            Date date = new Date();
            long timeInterval = date.getTime();
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), 0);
                version = pInfo.versionName;
                deviceVersion = Build.VERSION.RELEASE;
                model = Build.DEVICE;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            HashMap<String, String> jsonData = new HashMap<>();
            @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            jsonData.put("DeviceID", androidId);
            jsonData.put("DeviceModel", model);
            jsonData.put("DevicePlatform", "Android");
            jsonData.put("DeviceToken", Globals.getInstance().storage_loadString("TokenID"));
            jsonData.put("DeviceVersion", deviceVersion);
            jsonData.put("LoginDate", "/Date("+timeInterval+")/");
            jsonData.put("ProjectID", Globals.getInstance().storage_loadString("ProjectID"));
            jsonData.put("UserName", Globals.getInstance().storage_loadString("UserName"));

            jsonDevicePost(jsonData);
        }else{
            jsonDevicePostLiveData.postValue("false");
        }
    }

    public void jsonDevicePost(HashMap<String, String> _jData) {
        Call<BaseResponse> call = RestService.getApi().postDeviceInfo(_jData);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse resp = null;
                if (response.isSuccessful()) {
                    resp = response.body();
                    if (resp != null) {
                        if (resp.getServiceStatus().equals("SUCCESS")) {
                            syncProjects();
                        }else{
                            jsonDevicePostLiveData.postValue(resp.getServiceMessage());
                        }
                    }else{
                        jsonDevicePostLiveData.postValue("false");
                    }
                } else {
                    try {
                        String error = response.errorBody().string();
                        jsonDevicePostLiveData.postValue(error);
                        Log.e(TAG, error);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                jsonDevicePostLiveData.postValue(t.getLocalizedMessage());
            }
        });

    }

    public void syncProjects() {
        final HashMap<String, String> request = new HashMap<>();
        request.put("FirsName", Globals.getInstance().storage_loadString("UserName"));
        request.put("LastName", Globals.getInstance().storage_loadString("UserName"));
        request.put("PaceID", Globals.getInstance().storage_loadString("ProjectID"));
        request.put("ProjectID", Globals.getInstance().storage_loadString("ProjectID"));
        request.put("Username", Globals.getInstance().storage_loadString("UserName"));
        request.put("password", Globals.getInstance().storage_loadString("UserName"));
        Call<List<ProjectModel>> call = RestService.getApi().getProjectsByProjectID(request);
        call.enqueue(new Callback<List<ProjectModel>>() {
            @Override
            public void onResponse(Call<List<ProjectModel>> call, Response<List<ProjectModel>> response) {
                List<ProjectModel> resp = new ArrayList<>();
                if (response.isSuccessful()) {
                    resp = response.body();
                    if (resp != null) {
                        if (resp.size() > 0) {
                            if (resp.get(0).getServiceMessage().equals("SUCCESS")) {
                                ProjectModel projectModel = resp.get(0);
                                Projects projects = new Projects();
                                projects.setProjectID(projectModel.getProjectID());
                                projects.setProjectName(projectModel.getProjectName());
                                projects.setCasperID(projectModel.getCasprid());
                                projects.setPaceID(projectModel.getProjectID());
                                projects.setDescription(projectModel.getDescription());
                                projects.setFirstName(projectModel.getFirstName());
                                projects.setLastName(projectModel.getLastName());
                                App.getApp().getProjectsRepository().saveOrUpdateProject(projects);
                                syncCategories(projectModel);
                            }
                        }else{
                            jsonDevicePostLiveData.postValue("false");
                        }
                    }else{
                        jsonDevicePostLiveData.postValue("false");
                    }
                } else {
                    try {
                        String error = response.errorBody().string();
                        jsonDevicePostLiveData.postValue(error);
                        Log.e(TAG, error);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProjectModel>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                jsonDevicePostLiveData.postValue(t.getLocalizedMessage());
            }
        });

    }

    public void syncCategories(ProjectModel projectModel) {
        Call<List<CategoryModel>> call = RestService.getApi().getProjectCategories(projectModel.getProjectID());
        call.enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(Call<List<CategoryModel>> call, Response<List<CategoryModel>> response) {
                List<CategoryModel> resp = new ArrayList<>();
                if (response.isSuccessful()) {
                    resp = response.body();
                    assert resp != null;
                    if (resp.size() > 0) {
                        for (CategoryModel categoryModel : resp) {
                            Categories categories = new Categories();
                            categories.setProjectID(categoryModel.getProjectID());
                            categories.setCategoryID(categoryModel.getCategoryID());
                            categories.setCategoryName(categoryModel.getCategoryName());
                            categories.setParentCategoryID(categoryModel.getParentCategoryID());
                            categories.setSortOrder(categoryModel.getSortOrder());
                            categories.setContainsSectorPosition(categoryModel.isContainsSectorPosition());
                            App.getApp().getCategoriesRepository().saveOrUpdateCategory(categories);
                        }
                        syncSector();
                    }else{
                        jsonDevicePostLiveData.postValue("");
                    }
                } else {
                    try {
                        String error = response.errorBody().string();
                        jsonDevicePostLiveData.postValue(error);
                        Log.e(TAG, error);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                jsonDevicePostLiveData.postValue(t.getLocalizedMessage());
            }
        });

    }

    public void syncSector() {
        Call<List<Sector>> call = RestService.getApi().getSector();
        call.enqueue(new Callback<List<Sector>>() {
            @Override
            public void onResponse(Call<List<Sector>> call, Response<List<Sector>> response) {
                List<Sector> resp = new ArrayList<>();
                if (response.isSuccessful()) {
                    resp = response.body();
                    assert resp != null;
                    if (resp.size() > 0) {
                        for (Sector sector : resp) {
                            App.getApp().getSectorRepository().saveOrUpdateSector(sector);
                        }
                    }
                    syncPosition();
                } else {
                    try {
                        String error = response.errorBody().string();
                        jsonDevicePostLiveData.postValue(error);
                        Log.e(TAG, error);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Sector>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                jsonDevicePostLiveData.postValue(t.getLocalizedMessage());
            }
        });

    }

    public void syncPosition() {
        Call<List<Position>> call = RestService.getApi().getSectorPosition();
        call.enqueue(new Callback<List<Position>>() {
            @Override
            public void onResponse(Call<List<Position>> call, Response<List<Position>> response) {
                List<Position> resp = new ArrayList<>();
                if (response.isSuccessful()) {
                    resp = response.body();
                    assert resp != null;
                    if (resp.size() > 0) {
                        for (Position position : resp) {
                            App.getApp().getPositionRepository().saveOrUpdatePosition(position);
                        }
                    }
                    jsonDevicePostLiveData.postValue("");
                } else {
                    try {
                        String error = response.errorBody().string();
                        jsonDevicePostLiveData.postValue(error);
                        Log.e(TAG, error);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Position>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                jsonDevicePostLiveData.postValue(t.getLocalizedMessage());
            }
        });

    }

    public void getProjects() {
        List<ProjectDisplayModel> projects = App.getApp().getProjectsRepository().getDisplayProject();
        if (projects.size() > 0) {
            Globals.getInstance().projectsGlobalArray.clear();
            for (ProjectDisplayModel model : projects) {
                Globals.getInstance().projectsGlobalArray.add(model.getProjectID());
            }
            Log.e("getProject", String.valueOf(projects));
            projectDisplayModels.postValue(projects);
        }else{
            projectDisplayModels.postValue(new ArrayList<ProjectDisplayModel>());
        }
    }
    public void getProjectsArray() {
        List<ProjectDisplayModel> projects = App.getApp().getProjectsRepository().getDisplayProject();
        if (projects.size() > 0) {
            Globals.getInstance().projectsGlobalArray.clear();
            for (ProjectDisplayModel model : projects) {
                Globals.getInstance().projectsGlobalArray.add(model.getProjectID());
            }
            Log.e("getProject", String.valueOf(projects));
            getProjectArrayData.postValue(true);
        }else{
            getProjectArrayData.postValue(false);
        }
    }



    public void getRejects() {
        List<RejectDisplayModel> rejects = App.getApp().getProjectsRepository().getDisplayReject();
        if (rejects.size() > 0) {
            rejectDisplayModels.postValue(rejects);
        }else{
            rejectDisplayModels.postValue(new ArrayList<RejectDisplayModel>());
        }
    }

    public boolean checkConnection() {
        return true;
    }

    public MutableLiveData<List<ProjectDisplayModel>> getProjectDisplayModels() {
        return projectDisplayModels;
    }

    public void setProjectDisplayModels(MutableLiveData<List<ProjectDisplayModel>> projectDisplayModels) {
        this.projectDisplayModels = projectDisplayModels;
    }

    public MutableLiveData<List<RejectDisplayModel>> getRejectDisplayModels() {
        return rejectDisplayModels;
    }

    public void setRejectDisplayModels(MutableLiveData<List<RejectDisplayModel>> rejectDisplayModels) {
        this.rejectDisplayModels = rejectDisplayModels;
    }

    public void uploadDataToServer() {
        if (!Globals.getInstance().storage_loadString("SYNC").equals("")) {
            return;
        }
        showProgress.postValue(true);
        Globals.getInstance().storage_saveObject("SYNC", "true");
        long actualDate = new Date().getTime();
        Globals.getInstance().storage_saveObject("SyncTime", String.valueOf(actualDate));
        Log.e("UpdateDAtaToServer", String.valueOf(actualDate));
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Photos> datas = App.getApp().getProjectsRepository().getCapturedImages();
                if (datas.size() > 0){
                    Globals.getInstance().storage_saveObject("SYNC", "true");
                    Log.e("UpdateDAtaToServer", String.valueOf(datas));
                    uploadImages(datas);
                }else{
                    ArrayList<HashMap<String, String>> query= buildProjectPhotoQuery();
                    if (query.size() > 0) {
                        Log.e("UpdateDAtaToServer", String.valueOf(query));
                        syncProjectPhotos(query);
                    }else{
                        Log.e("UpdateDAtaToServer", "no data");
                        Globals.getInstance().storage_saveObject("SYNC", "");
                        showProgress.postValue(false);
                    }
                }
            }
        }).start();

    }

    public void uploadImages(List<Photos> datas) {
        intTotalImagesCount = datas.size();
        Globals.getInstance().uploadImageNamesList.clear();
        for (Photos photos : datas) {
            if (photos.getStatus() == 2) {
                Globals.getInstance().uploadImageNamesList.add(photos.getCapturedImageName());
            }else{
                Globals.getInstance().isResetPhotoAvailable = true;
            }
        }
        Globals.getInstance().uploadCount = 0;
        uploadImages();
    }

    public void uploadImages() {
        showProgress.postValue(false);
        if (Globals.getInstance().uploadImageNamesList.size() > 0) {
            uploadCapturedPhotos.postValue(Globals.getInstance().uploadCount);
            Log.e("UploadImages", String.valueOf(Globals.getInstance().uploadCount));
            String imagePath = "";
            String[] imagePathUrls = Globals.getInstance().uploadImageNamesList.get(Globals.getInstance().uploadCount).split("/");
            if (imagePathUrls.length > 0) {
                imagePath = imagePathUrls[imagePathUrls.length - 1];
            }
            if (!imagePath.contains(".jpeg") && !imagePath.contains("jpg")) {
                imagePath = imagePath + ".jpeg";
            }
            String strImageName = "";
            if (Globals.getInstance().storage_loadString("useAWSImageUpload") != null && !Globals.getInstance().storage_loadString("useAWSImageUpload").equals("")) {
                strImageName = String.format("%s%s", Globals.getInstance().storage_loadString("useAWSImageUpload"), imagePath);
            }else{
                strImageName = imagePath;
            }
            String filePath = Globals.getInstance().IMAGE_LOCATION_PATH + "/CapturedPhotos/" + imagePath;
            Map<String, String> fields = new HashMap<>();
            fields.put("policy", Globals.getInstance().policyBase64);
            fields.put("signature", Globals.getInstance().signature);
            fields.put("AWSAccessKeyId", Globals.getInstance().awsKey);
            fields.put("Content-Type", "image/jpeg");
            fields.put("acl", Globals.getInstance().acl);
            fields.put("key", strImageName);

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            OkHttpClient okClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            Retrofit retrofit = new Retrofit.Builder().client(okClient).baseUrl(Globals.getInstance().s3URI).build();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), new File(filePath));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", strImageName, requestFile);
            Map<String, RequestBody> parameters = new HashMap<>();
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                parameters.put(entry.getKey(), createPartFromString(entry.getValue()));
            }

            AmazonService service = retrofit.create(AmazonService.class);
            Call<ResponseBody> call = service.upload("", parameters, body);
            try {
                Response<ResponseBody> execute = call.execute();
                if (execute.code() == 204) {
                    Globals.getInstance().uploadCount = Globals.getInstance().uploadCount + 1;
                    uploadCapturedPhotos.postValue(Globals.getInstance().uploadCount);
                    if (Globals.getInstance().uploadCount != Globals.getInstance().uploadImageNamesList.size()) {
                        uploadImages();
                    }else{
                        Globals.getInstance().uploadCount = 0;
                        Log.e("UploadImage", "Upload Completed");
                        updatePhotosData();
                    }
                } else {
                    Log.e("AmazonS3Client", "unexpected http response: " + execute.code());
                    uploadError.postValue(true);
                }
            } catch (IOException e) {
                Log.e("AmazonS3Client", "upload error", e);
                uploadError.postValue(true);
            }

        }else{
            uploadError.postValue(true);
        }
    }

    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
    }

    public void updatePhotosData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Photos> photos = App.getApp().getProjectsRepository().getUpdatePhotos();
                ArrayList<HashMap<String, String>> jsonDatas = new ArrayList<>();
                for (Photos photo : photos) {
                    Date date = new Date();
                    long timeInterval = date.getTime();
                    deviceVersion = Build.VERSION.RELEASE;
                    model = Build.DEVICE;

                    HashMap<String, String> jsonData = new HashMap<>();
                    @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(App.getApp().getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    jsonData.put("DeviceID", androidId);
                    jsonData.put("DeviceModel", model);
                    jsonData.put("DevicePlatform", "Android");
                    jsonData.put("DeviceVersion", deviceVersion);
                    String takenDateString = photo.getTakenDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy, hh:mm:ss a");
                    Date takenDate = new Date();
                    try {
                        takenDate = dateFormat.parse(takenDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    jsonData.put("TakenDate", "/Date("+takenDate.getTime()+")/");
                    jsonData.put("IsAdhoc", String.valueOf(photo.getAdhoc()));
                    jsonData.put("ProjectID", photo.getProjectID());
                    jsonData.put("ItemID", photo.getItemID());
                    jsonData.put("CategoryID", photo.getCategoryID());
                    jsonData.put("SectorID", photo.getSectorID());
                    jsonData.put("PositionID", photo.getPositionID());
                    jsonData.put("ProjectPhotoID", photo.getProjectPhotoID());
                    jsonData.put("ItemName", photo.getItemName());
                    jsonData.put("Description", photo.getDescription());
                    String imagepath = "";
                    String[] imagepathurls = photo.getCapturedImageName().split("/");
                    if (imagepathurls.length > 0) {
                        imagepath = imagepathurls[imagepathurls.length - 1];
                    }
                    jsonData.put("ImagePath", imagepath);
                    jsonData.put("TakenBy", photo.getTakenBy());
                    jsonData.put("Latitude", photo.getLatitude());
                    jsonData.put("Longitude", photo.getLongitude());
                    jsonData.put("AdhocPhotoID", photo.getAdhocPhotoID());
                    jsonData.put("ApprovalStatus", String.valueOf(photo.getStatus()));

                    jsonDatas.add(jsonData);
                }

                Log.e("UpdatePhotos", String.valueOf(jsonDatas));
                updateProjectPhotos(jsonDatas);
            }
        }).start();

    }

    public void updateProjectPhotos(ArrayList<HashMap<String, String>> jsondatas) {
        Call<Object> call = RestService.getApi().updateProjectPhotos(jsondatas);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Globals.getInstance().isResetPhotoAvailable = false;
                    Log.e("UpdateProjectPhotos", String.valueOf(response));
                    updatePhotosDatabase();
                } else {
                    Globals.getInstance().storage_saveObject("SYNC", "");
                    uploadError.postValue(true);
                    try {
                        String error = response.errorBody().string();
                        Log.e(TAG, error);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                Globals.getInstance().storage_saveObject("SYNC", "");
                uploadError.postValue(true);
            }
        });

    }

    public void updatePhotosDatabase() {
        App.getApp().getProjectsRepository().updatePhotos1();
        App.getApp().getProjectsRepository().updatePhotos2();
        EventBus.getDefault().post(new EventPush("refresh", "category"));
        ArrayList<HashMap<String, String>> query= buildProjectPhotoQuery();
        if (query.size() > 0) {
            uploadError.postValue(false);
            Log.e("UpdatePhotos", String.valueOf(query));
            syncProjectPhotos(query);;
        }else{
            Globals.getInstance().storage_saveObject("SYNC", "");
            uploadError.postValue(false);
        }
    }

    public ArrayList<HashMap<String, String>> buildProjectPhotoQuery(){
        ArrayList<HashMap<String, String>> request = new ArrayList<>();
        if (Globals.getInstance().projectsGlobalArray.size() > 0) {
            for (String projectid : Globals.getInstance().projectsGlobalArray) {
                HashMap<String, String> param = new HashMap<>();
                param.put("CASPRID", projectid);
                String date = "0";
                if (Globals.getInstance().storage_loadString(projectid).equals("")) {
                    param.put("SyncDate", "/Date("+date +")/");
                }else{
                    date = Globals.getInstance().storage_loadString(projectid);
                    if (date.length() == 13) {
                        param.put("SyncDate", "/Date("+date +")/");
                    }else{
                        date = "0";
                        param.put("SyncDate", "/Date("+date +")/");
                    }
                }
                request.add(param);
            }
        }else{
        }
        return request;
    }

    public void syncProjectPhotos(final ArrayList<HashMap<String, String>> request) {
        showProgress.postValue(true);
        Globals.getInstance().storage_saveObject("SYNC", "true");
        Log.e("SyncProjectPhotos", String.valueOf(request));

        Call<List<ProjectPhotosModel>> call = RestService.getApi().getProjectPhotos(request);
        call.enqueue(new Callback<List<ProjectPhotosModel>>() {
            @Override
            public void onResponse(Call<List<ProjectPhotosModel>> call, Response<List<ProjectPhotosModel>> response) {
                List<ProjectPhotosModel> resp = new ArrayList<>();
                if (response.isSuccessful()) {
                    resp = response.body();
                    if (resp != null && resp.size() > 0) {
                        for (ProjectPhotosModel photosModel : resp) {
                            String convertedDate =  photosModel.getTakenDate();
                            if (convertedDate == null || convertedDate.equals("null")) {
                                convertedDate = "";
                            }else{
                                convertedDate = convertedDate.replace("/Date(", "");
                                convertedDate = convertedDate.replace(")/", "");
                                convertedDate = convertedDate.substring(0, 9);
                                long interval = Long.valueOf(convertedDate);
                                Date date = new Date(interval);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/dd/yyyy, h:mm:ss a");
                                convertedDate = simpleDateFormat.format(date);
                            }
                            String referenceImageName = "";
                            String[] referenceImageUrl = photosModel.getReferenceImageName().split("/");
                            if (referenceImageUrl.length > 0) {
                                referenceImageName = referenceImageUrl[referenceImageUrl.length - 1];
                            }
                            String imagepath = "";
                            String[] imageurl = photosModel.getImagePath().split("/");
                            if (imageurl.length > 0) {
                                imagepath = imageurl[imageurl.length - 1];
                            }
                            Photos photos = new Photos();
                            photos.setAdhoc(photosModel.isAdhoc());
                            photos.setAdhocPhotoID(photosModel.getAdhocPhotoID());
                            photos.setCapturedImageName(imagepath);
                            photos.setCategoryID(photosModel.getCategoryID());
                            photos.setComments(photosModel.getComments());
                            photos.setDescription(photosModel.getDescription());
                            photos.setItemID(photosModel.getItemID());
                            photos.setItemName(photosModel.getItemName());
                            photos.setLatitude(photosModel.getLatitude());
                            photos.setLongitude(photosModel.getLongitude());
                            photos.setParentCategoryID(photosModel.getCategoryRelations());
                            photos.setPositionID(photosModel.getPositionID());
                            photos.setProjectID(photosModel.getProjectID());
                            photos.setProjectPhotoID(photosModel.getProjectPhotoID());
                            photos.setReferenceImageName(referenceImageName);
                            photos.setSectorID(photosModel.getSectorID());
                            photos.setRequireSectorPosition(photosModel.isRequiresSectorPosition());
                            photos.setSortOrder(photosModel.getSortOrder());
                            photos.setStatus(photosModel.getApprovalStatus());
                            photos.setTakenBy(photosModel.getTakenBy());
                            photos.setTakenDate(convertedDate);
                            App.getApp().getPhotosRepository().saveOrUpdatePhotos(photos);
                            Log.e("syncProjectPhotos", String.valueOf(photos));

                        }
                        App.getApp().getPhotosRepository().deleteDeletedPhotos();
                    }
                    ArrayList<HashMap<String, String>> query= buildProjectPhotoQuery();
                    if (query.size() > 0 ) {
                        syncReferencPhotos(query);
                    }else{
                        Globals.getInstance().storage_saveObject("SYNC", "");
                        uploadError.postValue(false);
                        showProgress.postValue(false);
                    }
                } else {
                    Globals.getInstance().storage_saveObject("SYNC", "");
                    showProgress.postValue(false);
                    uploadError.postValue(false);
                    try {
                        String error = response.errorBody().string();
                        Log.e(TAG, error);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProjectPhotosModel>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                Globals.getInstance().storage_saveObject("SYNC", "");
                showProgress.postValue(false);
                uploadError.postValue(false);
            }
        });

    }

    public void syncReferencPhotos(final ArrayList<HashMap<String, String>> request) {
        if (Globals.getInstance().referenceImageNamesList.size() > 0) {
            Log.e("syncReferencPhotos", String.valueOf(Globals.getInstance().referenceImageNamesList));
            Globals.getInstance().referenceImageNamesList.clear();
        }
        {
            Globals.getInstance().storage_saveObject("SYNC", "true");
            Log.e("syncReferencPhotos", String.valueOf(Globals.getInstance().referenceImageNamesList.size()));
            Call<List<PhotoUrlModel>> call = RestService.getApi().getReferencePhotos(request);
            call.enqueue(new Callback<List<PhotoUrlModel>>() {
                @Override
                public void onResponse(Call<List<PhotoUrlModel>> call, Response<List<PhotoUrlModel>> response) {
                    List<PhotoUrlModel> resp = new ArrayList<>();
                    if (response.isSuccessful()) {
                        resp = response.body();
                        assert resp != null;
                        Globals.getInstance().referenceImageNamesList = new ArrayList<>();
                        Globals.getInstance().downloadCount = 0;

                        Log.e("reference Urls", String.valueOf(resp));
                        if (resp.size() > 0) {
                            for (PhotoUrlModel urlModel : resp) {
                                if (!Globals.getInstance().referenceImageNamesList.contains(urlModel.getStrURL())) {
                                    Globals.getInstance().referenceImageNamesList.add(urlModel.getStrURL());
                                }
                            }
                            downloadReferencePhotos();
                        }else{
                            ArrayList<HashMap<String, String>> query= buildProjectPhotoQuery();
                            syncCapturedPhotos(query);
                        }
                    } else {
                        Globals.getInstance().storage_saveObject("SYNC", "");
                        showProgress.postValue(false);
                        uploadError.postValue(false);
                        try {
                            String error = response.errorBody().string();
                            Log.e(TAG, error);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<PhotoUrlModel>> call, Throwable t) {
                    Log.e(TAG, t.getMessage(), t);
                    Globals.getInstance().storage_saveObject("SYNC", "");
                    showProgress.postValue(false);
                    uploadError.postValue(false);
                }
            });
        }

    }

    public void downloadReferencePhotos() {
        showProgress.postValue(false);
        Log.e("downloadReferencePhotos", String.valueOf(Globals.getInstance().downloadCount));
        Globals.getInstance().storage_saveObject("SYNC", "true");
        if (Globals.getInstance().referenceImageNamesList.size() > 0 && Globals.getInstance().referenceImageNamesList.size() > (Globals.getInstance().downloadCount)) {
            new DownloadReferenceImageTask().execute(Globals.getInstance().referenceImageNamesList.get(Globals.getInstance().downloadCount));
        }else{
            Globals.getInstance().referenceImageNamesList.clear();
            Globals.getInstance().downloadCount = 0;
            Globals.getInstance().capturedImageNamesList.clear();
            Globals.getInstance().downloadCapturedCount = 0;
            downloadReferencePhotos.postValue(Globals.getInstance().downloadCount);
            ArrayList<HashMap<String, String>> query= buildProjectPhotoQuery();
            if (query.size() > 0) {
                syncCapturedPhotos(query);
            }else{
                uploadError.postValue(false);
            }
        }
    }

    public void syncCapturedPhotos(final ArrayList<HashMap<String, String>> request) {
        if (Globals.getInstance().capturedImageNamesList.size() > 0) {
            Log.e("downloadcapturedPhotos", String.valueOf(Globals.getInstance().capturedImageNamesList));
            Globals.getInstance().capturedImageNamesList.clear();
        }
        showProgress.postValue(true);
        Globals.getInstance().storage_saveObject("SYNC", "true");
        Call<List<PhotoUrlModel>> call = RestService.getApi().getCapturedPhotos(request);
        call.enqueue(new Callback<List<PhotoUrlModel>>() {
            @Override
            public void onResponse(Call<List<PhotoUrlModel>> call, Response<List<PhotoUrlModel>> response) {
                List<PhotoUrlModel> resp = new ArrayList<>();
                if (response.isSuccessful()) {
                    resp = response.body();
                    assert resp != null;
                    Globals.getInstance().capturedImageNamesList.clear();
                    Globals.getInstance().downloadCapturedCount = 0;
                    Log.e("captured Urls", String.valueOf(resp));
                    if (resp.size() > 0) {
                        for (PhotoUrlModel urlModel : resp) {
                            if (!Globals.getInstance().capturedImageNamesList.contains(urlModel.getStrURL())) {
                                if (!urlModel.getStrURL().contains("noPhoto")) {
                                    Globals.getInstance().capturedImageNamesList.add(urlModel.getStrURL());
                                }
                            }
                        }
                        downloadCapturedPhotos();
                    }else {
                        showProgress.postValue(false);
                        uploadError.postValue(false);
                    }
                } else {
                    Globals.getInstance().storage_saveObject("SYNC", "");
                    showProgress.postValue(false);
                    uploadError.postValue(false);
                    try {
                        String error = response.errorBody().string();
                        Log.e(TAG, error);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PhotoUrlModel>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                Globals.getInstance().storage_saveObject("SYNC", "");
                showProgress.postValue(false);
                uploadError.postValue(false);
            }
        });

    }

    public void downloadCapturedPhotos() {
        showProgress.postValue(false);
        if (Globals.getInstance().capturedImageNamesList.size() > 0 && Globals.getInstance().capturedImageNamesList.size() > (Globals.getInstance().downloadCapturedCount)) {
            Log.e("downloadCapturedPhotos", String.valueOf(Globals.getInstance().downloadCapturedCount));
            Globals.getInstance().storage_saveObject("SYNC", "true");
            if (Globals.getInstance().capturedImageNamesList.get(Globals.getInstance().downloadCapturedCount).equals("") || Globals.getInstance().capturedImageNamesList.get(Globals.getInstance().downloadCapturedCount).contains("noPhoto.jpg")) {
                Globals.getInstance().downloadCapturedCount = Globals.getInstance().downloadCapturedCount + 1;
                if (Globals.getInstance().downloadCapturedCount < Globals.getInstance().capturedImageNamesList.size()) {
                    downloadCapturedPhotos.postValue(Globals.getInstance().downloadCapturedCount);
                    downloadCapturedPhotos();
                }else{
                    for (String project : Globals.getInstance().projectsGlobalArray) {
                        Globals.getInstance().storage_saveObject(project, Globals.getInstance().storage_loadString("SyncTime"));
                    }
                    Globals.getInstance().storage_saveObject("SYNC", "");
                    downloadCapturedPhotos.postValue(Globals.getInstance().downloadCapturedCount);
                    Globals.getInstance().capturedImageNamesList.clear();
                    Globals.getInstance().downloadCapturedCount = 0;
                    Globals.getInstance().referenceImageNamesList.clear();
                    Globals.getInstance().downloadCount = 0;
                    Globals.getInstance().storage_saveObject("SYNC", "");
                    uploadError.postValue(false);
                }
            }else{
                new DownloadCaptureImageTask().execute(Globals.getInstance().capturedImageNamesList.get(Globals.getInstance().downloadCapturedCount));
            }
        }else{
            Globals.getInstance().capturedImageNamesList.clear();
            Globals.getInstance().downloadCapturedCount = 0;
            Globals.getInstance().referenceImageNamesList.clear();
            Globals.getInstance().downloadCount = 0;
            for (String project : Globals.getInstance().projectsGlobalArray) {
                Globals.getInstance().storage_saveObject(project, Globals.getInstance().storage_loadString("SyncTime"));
            }
            Globals.getInstance().storage_saveObject("SYNC", "");
            downloadCapturedPhotos.postValue(Globals.getInstance().downloadCapturedCount);
            uploadError.postValue(false);
        }
    }

    private String tempGalleryPath(String imageNameStr) {
        String[] pathComponents = imageNameStr.split("_");
        String projectID = pathComponents[0];
        String path = "";
        if (projectID.length() > 0) {
            path = "/" + projectID;
        }
        else {
            Crashlytics.getInstance().crash();
        }

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        ApplicationInfo applicationInfo = App.getApp().getBaseContext().getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        String appName = stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : App.getApp().getBaseContext().getString(stringId);
        if (appName.length() < 1) {
            Crashlytics.getInstance().crash();
        }
        String filePath = storageDir.getAbsolutePath() + "/" + appName + path;
        return filePath;
    }

    public MutableLiveData<Boolean> getJsonDeleteProject() {
        return jsonDeleteProject;
    }

    public void setJsonDeleteProject(MutableLiveData<Boolean> jsonDeleteProject) {
        this.jsonDeleteProject = jsonDeleteProject;
    }

    public MutableLiveData<List<CategoryDisplayModel>> getCategoryDisplayModels() {
        return categoryDisplayModels;
    }

    public void setCategoryDisplayModels(MutableLiveData<List<CategoryDisplayModel>> categoryDisplayModels) {
        this.categoryDisplayModels = categoryDisplayModels;
    }

    public MutableLiveData<List<PhotoRemainingModel>> getPhotoRemainingModels() {
        return photoRemainingModels;
    }

    public void setPhotoRemainingModels(MutableLiveData<List<PhotoRemainingModel>> photoRemainingModels) {
        this.photoRemainingModels = photoRemainingModels;
    }

    public MutableLiveData<List<CategoryHeaderModel>> getCategoryDisplayHeaderModels() {
        return categoryDisplayHeaderModels;
    }

    public void setCategoryDisplayHeaderModels(MutableLiveData<List<CategoryHeaderModel>> categoryDisplayHeaderModels) {
        this.categoryDisplayHeaderModels = categoryDisplayHeaderModels;
    }

    public MutableLiveData<Integer> getUploadCapturedPhotos() {
        return uploadCapturedPhotos;
    }

    public void setUploadCapturedPhotos(MutableLiveData<Integer> uploadCapturedPhotos) {
        this.uploadCapturedPhotos = uploadCapturedPhotos;
    }

    public MutableLiveData<Boolean> getUploadError() {
        return uploadError;
    }

    public void setUploadError(MutableLiveData<Boolean> uploadError) {
        this.uploadError = uploadError;
    }

    public MutableLiveData<Boolean> getGetNextItem() {
        return getNextItem;
    }

    public void setGetNextItem(MutableLiveData<Boolean> getNextItem) {
        this.getNextItem = getNextItem;
    }

    public MutableLiveData<Boolean> getShowProgress() {
        return showProgress;
    }

    public void setShowProgress(MutableLiveData<Boolean> showProgress) {
        this.showProgress = showProgress;
    }

    public MutableLiveData<String> getGetLocationMatrix() {
        return getLocationMatrix;
    }

    public void setGetLocationMatrix(MutableLiveData<String> getLocationMatrix) {
        this.getLocationMatrix = getLocationMatrix;
    }

    public MutableLiveData<Boolean> getGetProjectArrayData() {
        return getProjectArrayData;
    }

    public void setGetProjectArrayData(MutableLiveData<Boolean> getProjectArrayData) {
        this.getProjectArrayData = getProjectArrayData;
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadReferenceImageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
        }

        @Override
        protected String doInBackground(String... URL) {

            String imageURL = URL[0];

            URL url = null;
            try {
                String urlstring = imageURL;//Globals.getInstance().referenceImageNamesList.get(Globals.getInstance().downloadCount);
                urlstring = urlstring.replace("d2cagqgd5mtm1q.cloudfront.net", "s3.amazonaws.com/closeout-app-content");
                String imagepath = "";
                String[] imageurl = urlstring.split("/");
                if (imageurl.length > 0) {
                    imagepath = imageurl[imageurl.length - 1];
                }
                if (imagepath.equals("")) {
                    return "";
                }else{
                    url = new URL(urlstring);
                    InputStream in = new BufferedInputStream(url.openStream());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int n = in.read(buf);
                    while (n!=-1)
                    {
                        out.write(buf, 0, n);
                        n=in.read(buf);
                    }
                    out.close();
                    in.close();
                    byte[] response = out.toByteArray();

                    String dirstr = Globals.getInstance().IMAGE_LOCATION_PATH + "/ReferencePhotos/";
                    File dir = new File(dirstr);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File file = new File(dir, imagepath);
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(response);
                    fos.close();

                    return imageURL;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return e.getLocalizedMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Set the bitmap into ImageView
            Log.e(TAG, result);
            Globals.getInstance().downloadCount = Globals.getInstance().downloadCount + 1;
            if (Globals.getInstance().downloadCount < Globals.getInstance().referenceImageNamesList.size()) {
                downloadReferencePhotos();
                downloadReferencePhotos.postValue(Globals.getInstance().downloadCount);
            }else{
                ArrayList<HashMap<String, String>> query= buildProjectPhotoQuery();
                if (query.size() > 0 ) {
                    syncCapturedPhotos(query);
                }
                if (Globals.getInstance().downloadCount > Globals.getInstance().referenceImageNamesList.size()) {
                    Globals.getInstance().downloadCount = Globals.getInstance().referenceImageNamesList.size();
                }
                downloadReferencePhotos.postValue(Globals.getInstance().downloadCount);
            }
        }
    }

    public MutableLiveData<Integer> getDownloadReferencePhotos() {
        return downloadReferencePhotos;
    }

    public MutableLiveData<Integer> getDownloadCapturedPhotos() {
        return downloadCapturedPhotos;
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadCaptureImageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
        }

        @Override
        protected String doInBackground(String... URL) {

            String imageURL = URL[0];

            try {
                //                urlstring = urlstring.replace("d2cagqgd5mtm1q.cloudfront.net", "s3.amazonaws.com/closeout-app-content");
                if (imageURL.contains("noPhoto")) {
                    return imageURL;
                }
                String imagepath = "";
                String[] imageurl = imageURL.split("/");
                if (imageurl.length > 0) {
                    imagepath = imageurl[imageurl.length - 1];
                }
                if (imagepath.equals("")) {
                    return "";
                }else{
                    URL url = null;
                    url = new URL(imageURL);
                    InputStream in = new BufferedInputStream(url.openStream());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int n = in.read(buf);
                    while (n!=-1)
                    {
                        out.write(buf, 0, n);
                        n=in.read(buf);
                    }
                    out.close();
                    in.close();
                    byte[] response = out.toByteArray();

//                    String filePath = Globals.getInstance().IMAGE_LOCATION_PATH + "/CapturedPhotos/" + imagepath;
                    String dirstr = Globals.getInstance().IMAGE_LOCATION_PATH + "/CapturedPhotos/";
                    File dir = new File(dirstr);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File file = new File(dir, imagepath);
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(response);
                    fos.close();

                    // copy to gallery
                    dirstr = tempGalleryPath(imagepath);
                    dir = new File(dirstr);
                    if (!dir.exists()) {
                        if (!dir.mkdirs()) {
                            Crashlytics.getInstance().crash();
                        }
                    }
                    File noNameFile = new File(dir, ".nomedia");
                    if (noNameFile.exists()) {
                        Crashlytics.getInstance().crash();
                    }

                    file = new File(dir, imagepath);
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    fos.write(response);
                    fos.close();

                    return imageURL;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Crashlytics.getInstance().crash();
                return e.getLocalizedMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Set the bitmap into ImageView
            Log.e(TAG, result);
            Globals.getInstance().downloadCapturedCount = Globals.getInstance().downloadCapturedCount + 1;
            if (Globals.getInstance().downloadCapturedCount < Globals.getInstance().capturedImageNamesList.size()) {
                downloadCapturedPhotos.postValue(Globals.getInstance().downloadCapturedCount);
                downloadCapturedPhotos();
            }else{
                for (String project : Globals.getInstance().projectsGlobalArray) {
                    Globals.getInstance().storage_saveObject(project, Globals.getInstance().storage_loadString("SyncTime"));
                }
                Globals.getInstance().storage_saveObject("SYNC", "");
                downloadCapturedPhotos.postValue(Globals.getInstance().downloadCapturedCount);
            }
        }
    }

    public void deleteProject(Context context, String projectID, String caspID) {
        showProgress.postValue(true);
        Globals.getInstance().storage_saveObject("SYNC", "true");
        Date date = new Date();
        long timeInterval = date.getTime();
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
            deviceVersion = Build.VERSION.RELEASE;
            model = Build.DEVICE;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        HashMap<String, String> jsonData = new HashMap<>();
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        jsonData.put("DeviceID", androidId);
        jsonData.put("DeviceModel", model);
        jsonData.put("DevicePlatform", "Android");
        jsonData.put("DeviceToken", Globals.getInstance().storage_loadString("TokenID"));
        jsonData.put("DeviceVersion", deviceVersion);
        jsonData.put("LoginDate", "/Date("+timeInterval+")/");
        jsonData.put("CreatedDate", "/Date("+timeInterval+")/");
        jsonData.put("IsDeleted", "true");
        jsonData.put("ProjectID", Globals.getInstance().storage_loadString("DeleteProjectID"));
        jsonData.put("UserName", Globals.getInstance().storage_loadString("UserName"));

        Call<BaseResponse> call = RestService.getApi().deleteProject(jsonData);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse resp = null;
                if (response.isSuccessful()) {
                    resp = response.body();
                    if (resp != null) {
                        if (resp.getServiceStatus().equals("SUCCESS")) {
                            deleteDatabaseProject();
                        }else{
                            jsonDeleteProject.postValue(false);
                            showProgress.postValue(false);
                            Globals.getInstance().storage_saveObject("SYNC", "");
                        }
                    }else{
                        jsonDeleteProject.postValue(false);
                        showProgress.postValue(false);
                        Globals.getInstance().storage_saveObject("SYNC", "");
                    }
                } else {
                    jsonDeleteProject.postValue(false);
                    showProgress.postValue(false);
                    Globals.getInstance().storage_saveObject("SYNC", "");
                    try {
                        String error = response.errorBody().string();
                        Log.e(TAG, error);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                jsonDeleteProject.postValue(false);
                showProgress.postValue(false);
                Globals.getInstance().storage_saveObject("SYNC", "");
            }
        });
    }

    public void deleteDatabaseProject() {
        String deleteProjectID = Globals.getInstance().storage_loadString("DeleteProjectID");
        App.getApp().getProjectsRepository().deleteProject(deleteProjectID);
        Globals.getInstance().projectsGlobalArray.remove(deleteProjectID);
        Globals.getInstance().storage_removeItem(deleteProjectID);
        Globals.getInstance().storage_saveObject("SYNC", "");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jsonDeleteProject.postValue(true);
        showProgress.postValue(false);
    }

    public void getPhotoRemainingCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Globals.getInstance().navigationStack.size() > 0) {
                    String projectID = Globals.getInstance().navigationStack.get(Globals.getInstance().navigationStack.size() - 1).getProjectID();
                    photoRemainingModels.postValue(App.getApp().getProjectsRepository().getPhotoRemainingCount(projectID));
                }
            }
        }).start();
    }

    public void getCategoryHeaderCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sectorID = Globals.getInstance().navigationStack.get(Globals.getInstance().navigationStack.size() - 1).getSectorID();
                String positionID = Globals.getInstance().navigationStack.get(Globals.getInstance().navigationStack.size() - 1).getPositionID();
                String parentID = Globals.getInstance().navigationStack.get(Globals.getInstance().navigationStack.size() - 1).getParentID();
                String projectID = Globals.getInstance().navigationStack.get(Globals.getInstance().navigationStack.size() - 1).getProjectID();
                if (sectorID.equals("0") || sectorID.equals("99999")) {
                    categoryDisplayHeaderModels.postValue(App.getApp().getProjectsRepository().getCategoryHeader1(projectID, parentID));
                }else if ((!sectorID.equals("0") || sectorID.equals("99999")) && (positionID.equals("0") || positionID.equals("99999"))) {
                    categoryDisplayHeaderModels.postValue(App.getApp().getProjectsRepository().getCategoryHeader2(projectID, parentID, sectorID));
                }else if ((!sectorID.equals("0") || sectorID.equals("99999")) && (!positionID.equals("0") || positionID.equals("99999"))) {
                    categoryDisplayHeaderModels.postValue(App.getApp().getProjectsRepository().getCategoryHeader3(projectID, parentID, positionID, sectorID));
                }
            }
        }).start();
    }

    public void getCategoryDisplay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sectorID = Globals.getInstance().navigationStack.get(Globals.getInstance().navigationStack.size() - 1).getSectorID();
                String positionID = Globals.getInstance().navigationStack.get(Globals.getInstance().navigationStack.size() - 1).getPositionID();
                String parentID = Globals.getInstance().navigationStack.get(Globals.getInstance().navigationStack.size() - 1).getParentID();
                String projectID = Globals.getInstance().navigationStack.get(Globals.getInstance().navigationStack.size() - 1).getProjectID();
                boolean requiredSectorPosition = Globals.getInstance().navigationStack.get(Globals.getInstance().navigationStack.size() - 1).getRequireSectorPosition();
                boolean check1 = !requiredSectorPosition;
                boolean check2 = sectorID.equals("0") || sectorID.equals("99999");
                boolean check3 = positionID.equals("0") || positionID.equals("99999");
                ArrayList<CategoryDisplayModel> categoryDisplayModelArrayList = new ArrayList<>();
                ArrayList<CategoryDisplayModel> realCategoryList = new ArrayList<>();
                ArrayList<CategoryDisplayModel> rejectedList = new ArrayList<>();
                ArrayList<CategoryDisplayModel> todoList = new ArrayList<>();
                ArrayList<CategoryDisplayModel> pendingList = new ArrayList<>();
                ArrayList<CategoryDisplayModel> approvedList = new ArrayList<>();
                ArrayList<CategoryDisplayModel> categoryList = new ArrayList<>();

                if (parentID.equals("0") || requiredSectorPosition) {
                    categoryDisplayModelArrayList = (ArrayList<CategoryDisplayModel>) App.getApp().getProjectsRepository().getCategoryDisplay1(projectID, parentID);
                }else if (check1 && check2 && check3) {
                    categoryDisplayModelArrayList = (ArrayList<CategoryDisplayModel>) App.getApp().getProjectsRepository().getCategoryDisplay2(projectID, parentID);
                }else if ((!sectorID.equals("0") || sectorID.equals("99999")) && (positionID.equals("0") || positionID.equals("99999"))) {
                    categoryDisplayModelArrayList = (ArrayList<CategoryDisplayModel>) App.getApp().getProjectsRepository().getCategoryDisplay3(projectID, parentID, sectorID);
                }else {
                    categoryDisplayModelArrayList = (ArrayList<CategoryDisplayModel>) App.getApp().getProjectsRepository().getCategoryDisplay4(projectID, parentID, sectorID, positionID);
                }

                for (CategoryDisplayModel catModel: categoryDisplayModelArrayList) {
                    List<CategoryHeaderModel> headerModels = new ArrayList<>();
                    if (!catModel.getISectorID().equals("0") && !catModel.getISectorID().equals("")) {
                        String catID = catModel.getISectorID();
                        headerModels = App.getApp().getProjectsRepository().getCategoryDisplayCount1(projectID, parentID, catID);
                    }else if (!catModel.getIPositionID().equals("0") && !catModel.getIPositionID().equals("")) {
                        String catID = catModel.getIPositionID();
                        headerModels = App.getApp().getProjectsRepository().getCategoryDisplayCount2(projectID, parentID, sectorID, catID);
                    }else {
                        String catID = catModel.getPCategoryID();
                        if (catID == null) {
                            catID = parentID;
                        }
                        headerModels = App.getApp().getProjectsRepository().getCategoryDisplayCount3(projectID, catID);
                    }

                    Integer totalCount = headerModels.get(0).getTotal();
                    Integer approvedCount = headerModels.get(0).getApproved();
                    Integer rejectedCount = headerModels.get(0).getRejected();
                    Integer takenCount = headerModels.get(0).getTaken();
                    Integer reqyiredCount = totalCount - approvedCount - rejectedCount - takenCount;
                    Double approvedPercent = 0.0;
                    Double rejectedPercent = 0.0;
                    Double takenPercent = 0.0;
                    if (totalCount > 0) {
                        takenPercent = (takenCount * 100.0) / totalCount;
                        approvedPercent = (approvedCount * 100.0) / totalCount;
                        rejectedPercent = (rejectedCount * 100.0) / totalCount;
                    }
                    CategoryDisplayModel catDisplayModel = catModel;
                    catDisplayModel.set_taken(takenCount);
                    catDisplayModel.set_approved(approvedCount);
                    catDisplayModel.set_rejected(rejectedCount);
                    catDisplayModel.set_required(reqyiredCount);
                    catDisplayModel.set_approvedPercent(approvedPercent);
                    catDisplayModel.set_rejectedPercent(rejectedPercent);
                    catDisplayModel.set_takenPercent(takenPercent);

                    if (catDisplayModel.getType().equals("Items")) {
                        if (catDisplayModel.getIStatus() == 1 || catDisplayModel.getIStatus() == 7) {
                            todoList.add(catDisplayModel);
                        }else if (catDisplayModel.getIStatus() == 2) {
                            pendingList.add(catDisplayModel);
                        }else if (catDisplayModel.getIStatus() == 3 || catDisplayModel.getIStatus() == 7) {
                            pendingList.add(catDisplayModel);
                        }else if (catDisplayModel.getIStatus() == 4 || catDisplayModel.getIStatus() == 7) {
                            approvedList.add(catDisplayModel);
                        }else if (catDisplayModel.getIStatus() == 0 || catDisplayModel.getIStatus() == 7) {
                            rejectedList.add(catDisplayModel);
                        }
                        else {
                            throw new IllegalArgumentException();
                        }
                    }else {
                        if (catDisplayModel.get_required() != 0 || catDisplayModel.get_rejected() != 0 || catDisplayModel.get_taken() != 0 || catDisplayModel.get_approved() != 0) {
                            categoryList.add(catDisplayModel);
                        }
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Comparator<CategoryDisplayModel> comparator = new Comparator<CategoryDisplayModel>() {
                        @Override
                        public int compare(CategoryDisplayModel o1, CategoryDisplayModel o2) {
                            String catName1 = "";
                            String catName2 = "";
                            catName1 = o1.getItemName();
                            catName2 = o2.getItemName();
                            return catName1.compareToIgnoreCase(catName2);
                        }
                    };

                    if (todoList.size() > 1) {
                        todoList.sort(comparator);
                    }

                    if (rejectedList.size() > 1) {
                        rejectedList.sort(comparator);
                    }

                    if (pendingList.size() > 1) {
                        pendingList.sort(comparator);
                    }

                    if (approvedList.size() > 1) {
                        approvedList.sort(comparator);
                    }

                    if (categoryList.size() > 1) {
                        categoryList.sort(new Comparator<CategoryDisplayModel>() {
                            @Override
                            public int compare(CategoryDisplayModel o1, CategoryDisplayModel o2) {
                                String catName1 = "";
                                String catName2 = "";
                                catName1 = o1.getCategoryName();
                                catName2 = o2.getCategoryName();
                                return catName1.compareToIgnoreCase(catName2);
                            }
                        });
                    }
                }

                realCategoryList.addAll(rejectedList);
                realCategoryList.addAll(todoList);
                realCategoryList.addAll(pendingList);
                realCategoryList.addAll(approvedList);
                realCategoryList.addAll(categoryList);

                categoryDisplayModels.postValue(realCategoryList);
            }
        }).start();
    }

    public void getDisplayItemCount(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sectorID = Globals.getInstance().storage_loadString("SectorID");;
                String positionID = Globals.getInstance().storage_loadString("PositionID");;
                String parentID = Globals.getInstance().storage_loadString("ParentID");;
                String projectID = Globals.getInstance().storage_loadString("ProjectID");;
                if (sectorID.equals("")) {
                    sectorID = "0";
                }
                if (positionID.equals("")) {
                    positionID = "0";
                }
                if (Integer.valueOf(sectorID) > 0) {
                    List<ItemCountModel> itemCountModel = App.getApp().getProjectsRepository().getItemCountByCategory1(projectID, parentID, sectorID, positionID);
                    if (itemCountModel.size() > 0) {
                        Globals.getInstance().storage_saveObject("ItemsCount", itemCountModel.get(0).getItemsCount());
                        if (itemCountModel.get(0).getItemID() != null) {
                            Globals.getInstance().storage_saveObject("ItemsCountItemID", itemCountModel.get(0).getItemID());
                        }else{
                            Globals.getInstance().storage_saveObject("ItemsCountItemID", "0");
                        }
                    }
                }else{
                    List<ItemCountModel> itemCountModel = App.getApp().getProjectsRepository().getItemCountByCategory2(projectID, parentID, sectorID, positionID);
                    if (itemCountModel.size() > 0) {
                        Globals.getInstance().storage_saveObject("ItemsCount", itemCountModel.get(0).getItemsCount());
                        if (itemCountModel.get(0).getAdhocPhotoID() != null) {
                            Globals.getInstance().storage_saveObject("ItemsCountItemID", itemCountModel.get(0).getAdhocPhotoID());
                        }else{
                            Globals.getInstance().storage_saveObject("ItemsCountItemID", "0");
                        }
                    }
                }
                getPhotoDetails();
            }
        }).start();
    }

    public void getPhotoDetails() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String adhocPhotoID = Globals.getInstance().storage_loadString("AdhocPhotoID");
                photoDetail.postValue(App.getApp().getProjectsRepository().getPhotoDetail(adhocPhotoID));
            }
        }).start();
    }

    @SuppressLint("HardwareIds")
    public void insertAdhocPhotoDataDB(final Photos photos) {
        String parentCategoryID = ",";
        for (int i = 0; i < Globals.getInstance().navigationStack.size(); i++) {
            if (i != 0 && i != Globals.getInstance().navigationStack.size()) {
                NavigationStack navigationStack = Globals.getInstance().navigationStack.get(i);
                parentCategoryID = String.format("%s,%s", parentCategoryID, navigationStack.getParentID());
            }
        }
        photos.setParentCategoryID(parentCategoryID);
        String uniqueId = UUID.randomUUID().toString();
        Log.e("UUID", uniqueId);
        photos.setAdhocPhotoID(uniqueId);
        photos.setProjectPhotoID(uniqueId);
        App.getApp().getProjectsRepository().insertAdhocPhotoDataDB(photos);
    }

    public void updateCapturedPhoto(final Photos photos) {
        App.getApp().getProjectsRepository().updateCapturedImage(photos);
    }

    public void getNextItemToDisplay() {
        final String projectID = Globals.getInstance().storage_loadString("ProjectID");
        final String parentID = Globals.getInstance().storage_loadString("ParentID");
        final String sectorID = Globals.getInstance().storage_loadString("SectorID");
        final String positionID = Globals.getInstance().storage_loadString("PositionID");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Integer.valueOf(sectorID) > 0) {
                    List<NextItemModel> models = App.getApp().getProjectsRepository().getNextItemToDisplay1(projectID, parentID, sectorID, positionID);
                    if (models.size() > 0) {
                        Globals.getInstance().storage_saveObject("ItemID", models.get(0).getItemID());
                        Globals.getInstance().storage_saveObject("AdhocPhotoID", models.get(0).getAdhocPhotoID());
                        getNextItem.postValue(true);
                    }else{
                        getNextItem.postValue(false);
                    }
                }else{
                    List<NextItemModel> models = App.getApp().getProjectsRepository().getNextItemToDisplay2(projectID, parentID, sectorID, positionID);
                    if (models.size() > 0) {
                        Globals.getInstance().storage_saveObject("ItemID", models.get(0).getItemID());
                        Globals.getInstance().storage_saveObject("AdhocPhotoID", models.get(0).getAdhocPhotoID());
                        getNextItem.postValue(true);
                    }else{
                        getNextItem.postValue(false);
                    }
                }
            }
        }).start();
    }

    public void getCategoryLocation(final String adhodPhotoID, String categoryRelationID) {
        String categoryIDList = "";
        String[] categoryIDs = categoryRelationID.split(",");
        for (int i = 0; i < categoryIDs.length; i++) {
            if (!categoryIDs[i].equals("")) {
                if (categoryIDList.equals("")) {
                    categoryIDList = String.format("%s", categoryIDs[i]);
                }else {
                    categoryIDList = String.format("%s, %s", categoryIDList, categoryIDs[i]);
                }
            }
        }
        final String finalCategoryIDList = categoryIDList;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<LocationMatrixModel> locationMatrixModels = App.getApp().getProjectsRepository().getLocationMatrix(adhodPhotoID, finalCategoryIDList);
                String locationMatrixString = "";
                for (int i = 0; i < locationMatrixModels.size(); i++) {
                    if (locationMatrixString.equals("")) {
                        locationMatrixString = String.format("%s", locationMatrixModels.get(i).getCategoryName());
                    }else {
                        locationMatrixString = String.format("%s >> %s", locationMatrixString, locationMatrixModels.get(i).getCategoryName());
                    }
                }
                getLocationMatrix.postValue(locationMatrixString);
            }
        }).start();
    }

}
