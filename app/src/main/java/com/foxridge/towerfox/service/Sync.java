package com.foxridge.towerfox.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.foxridge.towerfox.BuildConfig;
import com.foxridge.towerfox.service.response.BaseResponse;
import com.foxridge.towerfox.utils.Globals;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Sync {
    private static final String TAG = Sync.class.getName();
    private Context context;
    private static Sync instance;
    String version = "";
    String deviceVersion = "";
    String model = "";

    private Sync(){
    }

    public Sync(Context context){
        this.context = context;
    }

    public void init(Context context) {
        this.context = context;
    }

    public static synchronized Sync getInstance(){
        if(instance==null){
            instance=new Sync();
        }
        return instance;
    }

    public void sendDeviceInfo() {
        if (checkConnection()) {
            Date date = new Date();
            long timeInterval = date.getTime();
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), 0);
                version = BuildConfig.VERSION_NAME;
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
                    assert resp != null;
                    if (resp.getServiceStatus().equals("SUCCESS")) {
                        syncProjects();
                    }
                } else {
                    try {
                        String error = response.errorBody().string();
                        Log.e(TAG, error);
                        Gson gson = new Gson();
                        resp = gson.fromJson(error, BaseResponse.class);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
            }
        });

    }

    public void syncProjects() {
        HashMap<String, String> request = new HashMap<>();
        request.put("FirsName", Globals.getInstance().storage_loadString("UserName"));
        request.put("LastName", Globals.getInstance().storage_loadString("UserName"));
        request.put("PaceID", Globals.getInstance().storage_loadString("ProjectID"));
        request.put("ProjectID", Globals.getInstance().storage_loadString("ProjectID"));
        request.put("UserName", Globals.getInstance().storage_loadString("UserName"));
        request.put("password", Globals.getInstance().storage_loadString("UserName"));
    }

    public void getProject() {
    }

    public boolean checkConnection() {
        return true;
    }

}
