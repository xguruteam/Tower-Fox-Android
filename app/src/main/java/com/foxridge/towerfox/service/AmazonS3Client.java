package com.foxridge.towerfox.service;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AmazonS3Client {
    private static final String EMPTY_URL_TO_MAKE_RETROFIT_HAPPY = "";

    public boolean uploadMoment(Map<String, String> fields, String filePath, String s3BaseUrl) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient okClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder().client(okClient).baseUrl(s3BaseUrl + "/").build();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), new File(filePath));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fields.get("key"), requestFile);
        Map<String, RequestBody> parameters = new HashMap<>();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            parameters.put(entry.getKey(), createPartFromString(entry.getValue()));
        }

        AmazonService service = retrofit.create(AmazonService.class);
        Call<ResponseBody> call = service.upload(EMPTY_URL_TO_MAKE_RETROFIT_HAPPY, parameters, body);
        try {
            Response<ResponseBody> execute = call.execute();
            if (execute.code() == 204) {
                return true;
            } else {
                Log.e("AmazonS3Client", "unexpected http response: " + execute.code());
            }
        } catch (IOException e) {
            Log.e("AmazonS3Client", "upload error", e);
        }
        return false;
    }

    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
    }
}
