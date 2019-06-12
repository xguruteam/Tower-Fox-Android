package com.foxridge.towerfox.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.foxridge.towerfox.service.RestService;
import com.foxridge.towerfox.service.response.BaseResponse;
import com.foxridge.towerfox.utils.Globals;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsViewModel extends ViewModel {
    private static final String TAG = SettingsViewModel.class.getName();
    private MutableLiveData<BaseResponse> responseLogoutData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<String> serverIPLiveData = new MutableLiveData<>();
    private MutableLiveData<String> loggedUserData = new MutableLiveData<>();

    public void getServerIP () {
        String serverip = Globals.getInstance().storage_loadString("SERVER_IP");
        serverIPLiveData.postValue(serverip);
    }

    public void getUserName() {
        String username = Globals.getInstance().storage_loadString("UserName");
        loggedUserData.postValue(username);
    }
    public void logout(HashMap<String, String> request) {
        Call<BaseResponse> call = RestService.getApi().logout(request);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse resp = null;
                if (response.isSuccessful()) {
                    resp = response.body();
                } else {
                    try {
                        String error = response.errorBody().string();
                        Log.e(TAG, error);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
                responseLogoutData.postValue(resp);
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public MutableLiveData<BaseResponse> getResponseLogoutData() {
        return responseLogoutData;
    }

    public void setResponseLogoutData(MutableLiveData<BaseResponse> responseLogoutData) {
        this.responseLogoutData = responseLogoutData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void setErrorLiveData(MutableLiveData<String> errorLiveData) {
        this.errorLiveData = errorLiveData;
    }

    public MutableLiveData<String> getServerIPLiveData() {
        return serverIPLiveData;
    }

    public void setServerIPLiveData(MutableLiveData<String> serverIPLiveData) {
        this.serverIPLiveData = serverIPLiveData;
    }

    public MutableLiveData<String> getLoggedUserData() {
        return loggedUserData;
    }

    public void setLoggedUserData(MutableLiveData<String> loggedUserData) {
        this.loggedUserData = loggedUserData;
    }
}
