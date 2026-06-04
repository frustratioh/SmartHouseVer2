package com.example.smarthouse.api;

import com.example.smarthouse.model.dto.request.*;
import com.example.smarthouse.model.dto.response.*;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // 1. АВТОРИЗАЦИЯ И РЕГИСТРАЦИЯ
    @GET("users")
    Call<List<UserResponse>> login(
            @Query("email") String emailFilter,
            @Query("password_hash") String passwordFilter
    );

    @POST("users")
    Call<List<UserResponse>> register(@Body RegisterRequest request);

    @GET("users")
    Call<List<UserResponse>> getUser(@Query("id") String idFilter);

    @PATCH("users")
    Call<List<UserResponse>> updateUserAccount(
            @Query("id") String idFilter,
            @Body UpdateAccountRequest request
    );

    @PATCH("users")
    Call<ResponseBody> updateAddress(
            @Query("id") String idFilter,
            @Body Map<String, String> addressMap
    );

    @PATCH("users")
    Call<ResponseBody> updatePinCode(
            @Query("id") String idFilter,
            @Body UpdatePinRequest request
    );


    // 2. КОМНАТЫ И ТИПЫ КОМНАТ
    @GET("room_types")
    Call<List<RoomTypeResponse>> getRoomTypes();

    @GET("custom_room_types")
    Call<List<CustomRoomTypeResponse>> getCustomRoomTypes(@Query("user_id") String userIdFilter);

    @POST("custom_room_types")
    Call<List<CustomRoomTypeResponse>> createCustomRoomType(@Body CustomRoomTypeRequest request);

    @DELETE("custom_room_types")
    Call<Void> deleteCustomRoomType(@Query("custom_room_type_id") String filter);

    @GET("rooms")
    Call<List<RoomResponse>> getUserRooms(@Query("user_id") String userIdFilter);

    @POST("rooms")
    Call<ResponseBody> createRoom(@Body CreateRoomRequest request);

    @DELETE("rooms")
    Call<Void> deleteRoom(@Query("id") String filter);


    // 3. УСТРОЙСТВА
    @GET("device_types")
    Call<List<DeviceTypeResponse>> getDeviceTypes();

    @GET("devices")
    Call<List<DeviceResponse>> getRoomDevices(@Query("room_id") String roomIdFilter);

    @POST("devices")
    @Headers("Prefer: return=representation")
    Call<List<DeviceResponse>> createDevice(@Body CreateDeviceRequest request);

    @DELETE("devices")
    Call<Void> deleteDevice(@Query("device_id") String filter);


    // 4. НАСТРОЙКИ УСТРОЙСТВ
    @GET("device_settings")
    Call<List<DeviceSettingResponse>> getDeviceSettings(@Query("device_id") String deviceIdFilter);

    @POST("device_settings?on_conflict=device_id,parameter")
    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    Call<List<DeviceSettingResponse>> upsertDeviceSettings(@Body List<DeviceSettingRequest> settings);

    @PATCH("device_settings")
    Call<ResponseBody> updateDeviceSetting(
            @Query("device_id") String deviceIdFilter,
            @Query("parameter") String parameterFilter,
            @Body Map<String, String> valueMap
    );
}
