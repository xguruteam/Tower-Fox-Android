package com.foxridge.towerfox.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.foxridge.towerfox.service.RestService;
import com.foxridge.towerfox.service.response.AuthenticateModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private static final String TAG = LoginViewModel.class.getName();
    private MutableLiveData<AuthenticateModel> responseLoginLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public void login(String userName, String password) {
        HashMap<String, String> request = new HashMap<>();
        request.put("UserName", userName);
        request.put("Password", password);
        Call<AuthenticateModel> call = RestService.getApi().login(request);
        call.enqueue(new Callback<AuthenticateModel>() {
            @Override
            public void onResponse(Call<AuthenticateModel> call, Response<AuthenticateModel> response) {
                AuthenticateModel resp = null;
                if (response.isSuccessful()) {
                    resp = response.body();
                } else {
                    try {
                        String error = response.errorBody().string();
                        Log.e(TAG, error);
                        Gson gson = new Gson();
                        resp = gson.fromJson(error, AuthenticateModel.class);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
                responseLoginLiveData.postValue(resp);
            }

            @Override
            public void onFailure(Call<AuthenticateModel> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                errorLiveData.postValue(t.getMessage());
            }
        });
    }
    public MutableLiveData<AuthenticateModel> getResponseLoginLiveData() {
        return responseLoginLiveData;
    }

    public void setResponseLoginLiveData(MutableLiveData<AuthenticateModel> responseLoginLiveData) {
        this.responseLoginLiveData = responseLoginLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void setErrorLiveData(MutableLiveData<String> errorLiveData) {
        this.errorLiveData = errorLiveData;
    }
}
