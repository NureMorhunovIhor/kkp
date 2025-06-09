package com.kkp.nure.animalrescue.api.services;

import com.kkp.nure.animalrescue.api.requests.Disable2faRequest;
import com.kkp.nure.animalrescue.api.requests.Enable2faRequest;
import com.kkp.nure.animalrescue.api.requests.RegisterFcmRequest;
import com.kkp.nure.animalrescue.api.requests.UpdateLocationRequest;
import com.kkp.nure.animalrescue.api.requests.UpdateUserInfoRequest;
import com.kkp.nure.animalrescue.entities.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface UserService {
    @GET("user/info")
    Call<User> getUser();

    @PATCH("user/info")
    Call<User> editUser(@Body UpdateUserInfoRequest body);

    @POST("user/mfa/enable")
    Call<User> enable2fa(@Body Enable2faRequest body);

    @POST("user/mfa/disable")
    Call<User> disable2fa(@Body Disable2faRequest body);

    @POST("user/register-device")
    Call<Void> registerDevice(@Body RegisterFcmRequest body);

    @POST("user/location")
    Call<Void> sendLocation(@Body UpdateLocationRequest body);
}
