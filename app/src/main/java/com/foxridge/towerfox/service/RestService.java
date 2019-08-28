package com.foxridge.towerfox.service;

import android.annotation.SuppressLint;
import android.provider.Settings;
import android.util.Log;

import com.foxridge.towerfox.App;
import com.foxridge.towerfox.BuildConfig;
import com.foxridge.towerfox.service.response.AuthenticateModel;
import com.foxridge.towerfox.utils.Globals;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestService {

    private static final String BASE_URL = "https://api.budmeow.com";

    public static BackendApi getLogApi() {
        return getRetrofit("https://demo.foxdrive.net", getClient(HttpLoggingInterceptor.Level.BODY), getGson()).create(BackendApi.class);
    }

    public static void logOnServer(String message) {
        HashMap<String, String> request = new HashMap<>();
        request.put("ActionName", "Mobile App Photo Sync - Android");
        request.put("UserName", Globals.getInstance().storage_loadString("UserName"));
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(App.getApp().getContentResolver(), Settings.Secure.ANDROID_ID);
        request.put("DeviceId", androidId);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        request.put("MobileAppVersion", "Tower Fox for Android " + versionName + "(#" + versionCode + ")");
        request.put("ErrorMessage", message);
        Call<Void> call = RestService.getApi().logOnServer(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("LogOnServer", "Success");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i("LogOnServer", "Failure");
            }
        });
    }

    public static BackendApi getTestApi(String serverIP) {
        return getRetrofit(Globals.getInstance().getServiceConnectivityURL(serverIP), getClient(HttpLoggingInterceptor.Level.BODY), getGson()).create(BackendApi.class);
    }

    public static BackendApi getApi() {
        return getRetrofit(Globals.getInstance().getApplicationURL(), getClient(HttpLoggingInterceptor.Level.BODY), getGson()).create(BackendApi.class);
    }

    private static Retrofit getRetrofit(String url, OkHttpClient client, Gson gson) {
        if (!url.endsWith("/")) {
            url += "/";
        }
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private static Gson getGson() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Long.class, new LongTypeAdapter())
                .registerTypeAdapter(Integer.class, new IntegerTypeAdapter())
                .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
                .registerTypeAdapter(double.class, new DoubleTypeAdapter())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") //"2017-06-16T21:27:12Z"
                .create();
    }

    private static OkHttpClient getClient(HttpLoggingInterceptor.Level level) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(level);

        return new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                })
                .addInterceptor(logging)
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
    }

    static class IntegerTypeAdapter extends TypeAdapter<Integer> {
        @Override
        public void write(JsonWriter jsonWriter, Integer number) throws IOException {
            if (number == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(number);
        }

        @Override
        public Integer read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }

            try {
                String value = jsonReader.nextString();
                if ("".equals(value)) {
                    return 0;
                }
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }
    }

    static class LongTypeAdapter extends TypeAdapter<Long> {
        @Override
        public void write(JsonWriter jsonWriter, Long number) throws IOException {
            if (number == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(number);
        }

        @Override
        public Long read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }

            try {
                String value = jsonReader.nextString();
                if ("".equals(value)) {
                    return 0L;
                }
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }
    }

    static class DoubleTypeAdapter extends TypeAdapter<Double> {
        @Override
        public void write(JsonWriter jsonWriter, Double number) throws IOException {
            if (number == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(number);
        }

        @Override
        public Double read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }

            try {
                String value = jsonReader.nextString();
                if ("".equals(value)) {
                    return 0d;
                }
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }
    }
}
