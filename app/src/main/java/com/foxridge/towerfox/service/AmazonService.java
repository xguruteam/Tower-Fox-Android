package com.foxridge.towerfox.service;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface AmazonService {
    @Multipart
    @POST
    Call<ResponseBody> upload(@Url String url, @PartMap Map<String, RequestBody> params, @Part MultipartBody.Part file);
}
