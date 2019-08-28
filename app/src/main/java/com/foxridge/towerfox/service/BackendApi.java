package com.foxridge.towerfox.service;

import com.foxridge.towerfox.model.CategoryModel;
import com.foxridge.towerfox.model.PhotoUrlModel;
import com.foxridge.towerfox.model.Position;
import com.foxridge.towerfox.model.ProjectModel;
import com.foxridge.towerfox.model.ProjectPhotosModel;
import com.foxridge.towerfox.model.Sector;
import com.foxridge.towerfox.service.response.AuthenticateModel;
import com.foxridge.towerfox.service.response.BaseResponse;
import com.foxridge.towerfox.service.response.ServerResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BackendApi {
    String AUTH_TOKEN_HEADER = "Authorization";
    @GET("ServiceConnectivityTest")
    Call<ServerResponse>
    serverConnectivityTest();

    @POST("AuthenticateNSiteUser")
    Call<AuthenticateModel>
    login(@Body HashMap<String, String> request);

    @POST("DeleteDeviceInfoLogOut")
    Call<BaseResponse>
    logout(@Body HashMap<String, String> request);

    @POST("SaveDeviceInfo")
    Call<BaseResponse>
    postDeviceInfo(@Body HashMap<String, String> request);

    @POST("GetProjectsbyID")
    Call<List<ProjectModel>>
    getProjectsByProjectID(@Body HashMap<String, String> request);

    @GET("GetProjectCategorybyprjtID/{projectID}")
    Call<List<CategoryModel>>
    getProjectCategories(@Path("projectID") String projectID);

    @GET("GetSectors")
    Call<List<Sector>>
    getSector();

    @GET("GetSectorPosition")
    Call<List<Position>>
    getSectorPosition();

    @POST("GetProjectPhotosByProjectsSyncInfo")
    Call<List<ProjectPhotosModel>>
    getProjectPhotos(@Body ArrayList<HashMap<String, String>> request);

    @POST("GetReferencePhotosByProjectsSyncInfo")
    Call<List<PhotoUrlModel>>
    getReferencePhotos(@Body ArrayList<HashMap<String, String>> request);

    @POST("GetCapturedPhotosByProjectsSyncInfo")
    Call<List<PhotoUrlModel>>
    getCapturedPhotos(@Body ArrayList<HashMap<String, String>> request);

    @POST("DeleteDeviceInfoByProject")
    Call<BaseResponse>
    deleteProject(@Body HashMap<String, String> request);

    @POST("UpdateProjectPhotos")
    Call<Object>
    updateProjectPhotos(@Body ArrayList<HashMap<String, String>> request);

    @POST("LogSyncError")
    Call<Void>
    logOnServer(@Body HashMap<String, String> request);


//
//    @POST("login")
//    @FormUrlEncoded
//    Call<UserResponse> login(@Field("email") String email, @Field("password") String password);
//
//    @Multipart
//    @POST("register")
//    Call<UserResponse> register(
//            @Part("first_name") RequestBody firstName,
//            @Part("last_name") RequestBody lastName,
//            @Part("email") RequestBody email,
//            @Part("password") RequestBody password,
//            @Part("birthday") RequestBody birthday,
//            @Part("phone") RequestBody phone,
//            @Part("device_id") RequestBody deviceId,
//            @Part MultipartBody.Part avatar);
//
//    @POST("send_code")
//    @FormUrlEncoded
//    Call<VerifyPhoneResponse> getCode(@Header(AUTH_TOKEN_HEADER) String token, @Field("phone") String phone);
//
//    @POST("code")
//    @FormUrlEncoded
//    Call<VerifyPhoneResponse> sendCode(@Header(AUTH_TOKEN_HEADER) String token, @Field("phone") String phone, @Field("code") String code);
//
//    @POST("facebook")
//    @FormUrlEncoded
//    Call<UserResponse> facebook(@Field("access_token") String accessToken);
//
//    @Multipart
//    @POST("update")
//    Call<UserResponse> update(
//            @Header(AUTH_TOKEN_HEADER) String token,
//            @Part("email") RequestBody email,
//            @Part("first_name") RequestBody firstName,
//            @Part("last_name") RequestBody lastName,
//            @Part("birthday") RequestBody birthday,
//            @Part("phone") RequestBody phone,
//            @Part("device_id") RequestBody deviceId,
//            @Part MultipartBody.Part avatar);
//
//    @POST("verify_medical_card")
//    @FormUrlEncoded
//    Call<VerifyMedicResponse> verifyMedicCard(@Header(AUTH_TOKEN_HEADER) String token, @Field("medical_card_number") String medicalCardNumber);
//
//    @GET("details")
//    Call<UserResponse> getUserDetails(@Header(AUTH_TOKEN_HEADER) String token);
//
//    @POST("logout")
//    Call<BaseResponse> logout(@Header(AUTH_TOKEN_HEADER) String token);
//
//    @POST("nearby_retailer")
//    @FormUrlEncoded
//    Call<RetailerListResponse> getNearbyRetailers(@Header(AUTH_TOKEN_HEADER) String token, @Field("latitude") double latitude, @Field("longitude") double longitude);
//
//    @POST("item")
//    @FormUrlEncoded
//    Call<BudResponse> getBuds(@Header(AUTH_TOKEN_HEADER) String token, @Field("retailer_id") String retailerId);
//
//    @POST("order")
//    Call<OrderResponse> postOrder(@Header(AUTH_TOKEN_HEADER) String token, @Body OrderRequest orderRequest);
//
//    @POST("order_details")
//    @FormUrlEncoded
//    Call<OrderResponse> getOrderDetails(@Header(AUTH_TOKEN_HEADER) String token, @Field("id") String id);
//
//    @POST("limits")
//    Call<LimitsResponse> getTodayLimits(@Header(AUTH_TOKEN_HEADER) String token);
//
//    @POST("delivery")
//    @FormUrlEncoded
//    Call<DeliveryResponse> getDeliveryFee(
//            @Header(AUTH_TOKEN_HEADER) String token,
//            @Field("latitudeDelivery") double latitude,
//            @Field("longitudeDelivery") double longitude,
//            @Field("retailerId") String retailerId,
//            @Field("amount") BigDecimal basketSum);
//
//    @POST("all_orders")
//    Call<OrderListResponse> getAllOrders(@Header(AUTH_TOKEN_HEADER) String token);
//
//    @POST("search")
//    @FormUrlEncoded
//    Call<SearchProductResponse> searchProduct(@Header(AUTH_TOKEN_HEADER) String token, @Field("latitude") Double latitude, @Field("longitude") Double longitude, @Field("search") String query);
//
//    @POST("search_retailers")
//    @FormUrlEncoded
//    Call<RetailerListResponse> searchRetailers(@Header(AUTH_TOKEN_HEADER) String token, @Field("latitude") Double latitude, @Field("longitude") Double longitude, @Field("search") String query);
//
//    @POST("retailer")
//    @FormUrlEncoded
//    Call<RetailerResponse> getRetailer(@Header(AUTH_TOKEN_HEADER) String token, @Field("retailer_id") String retailerId);
//
//    @POST("add_favorite_retailer")
//    @FormUrlEncoded
//    Call<BaseResponse> addFavorite(@Header(AUTH_TOKEN_HEADER) String token, @Field("retailer_id") String retailerId);
//
//    @POST("delete_favorite_retailer")
//    @FormUrlEncoded
//    Call<BaseResponse> deleteFavorite(@Header(AUTH_TOKEN_HEADER) String token, @Field("retailer_id") String retailerId);
//
//    @POST("favorites_retailers")
//    Call<RetailerListResponse> getFavorites(@Header(AUTH_TOKEN_HEADER) String token);
//
//    @POST("premium_buds")
//    Call<RetailerListResponse> getPremiumBuds(@Header(AUTH_TOKEN_HEADER) String token);
//
//    @POST("deals_of_day_retailer")
//    @FormUrlEncoded
//    Call<RetailerListResponse> getDealsOfTheDayRetailer(@Header(AUTH_TOKEN_HEADER) String token, @Field("latitude") double latitude, @Field("longitude") double longitude);
//
//    @POST("doc")
//    @FormUrlEncoded
//    Call<UserResponse> sendDocImage(@Header(AUTH_TOKEN_HEADER) String token, @Field("imagebase64") String base64image);
//
//    @POST("push_id")
//    @FormUrlEncoded
//    Call<BaseResponse> registerPushId(@Header(AUTH_TOKEN_HEADER) String token, @Field("push_id") String pushId, @Field("device") int platform);
//
//    @POST("rating_driver")
//    @FormUrlEncoded
//    Call<BaseResponse> rateDriver(
//            @Header(AUTH_TOKEN_HEADER) String token,
//            @Field("rate") int rating,
//            @Field("comment") String comment,
//            @Field("order_id") String orderId,
//            @Field("driver_id") String driverId
//    );
//
//    @POST("rating_product")
//    @FormUrlEncoded
//    Call<BaseResponse> rateProduct(
//            @Header(AUTH_TOKEN_HEADER) String token,
//            @Field("product_id") String productId,
//            @Field("order_id") String orderId,
//            @Field("rate") int rate,
//            @Field("comment") String comment
//    );
//
//    @POST("deals_of_day_item")
//    @FormUrlEncoded
//    Call<BudResponse> getDealsProduct(@Header(AUTH_TOKEN_HEADER) String token, @Field("retailer_id") String retailerId);
}
