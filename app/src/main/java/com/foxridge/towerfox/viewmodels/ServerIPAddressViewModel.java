package com.foxridge.towerfox.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.foxridge.towerfox.service.RestService;
import com.foxridge.towerfox.service.response.ServerResponse;
import com.foxridge.towerfox.utils.Globals;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerIPAddressViewModel extends ViewModel {
    private static final String TAG = ServerIPAddressViewModel.class.getName();

    private MutableLiveData<Boolean> serverConnectivity = new MutableLiveData<>();


    public void checkServerConnectivtiyTest(String _serverIP) {

        Globals.getInstance().storage_saveObject("SYNC", "true");
        Call<ServerResponse> call = RestService.getTestApi(_serverIP).serverConnectivityTest();
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Server !");
                    response.body().save();
                    Globals.getInstance().storage_saveObject("SYNC", "");
                    serverConnectivity.postValue(true);

                } else {
                    try {
                        String error = response.errorBody().string();
                        Log.e(TAG, "server IP: " + error);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    Globals.getInstance().storage_saveObject("SYNC", "");
                    serverConnectivity.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                Globals.getInstance().storage_saveObject("SYNC", "");
                serverConnectivity.postValue(false);
            }
        });
    }
    public MutableLiveData<Boolean> getServerConnectivity() {
        return serverConnectivity;
    }

    public void setServerConnectivity(MutableLiveData<Boolean> serverConnectivity) {
        this.serverConnectivity = serverConnectivity;
    }
}
