package com.kkp.nure.animalrescue.api.services;

import com.kkp.nure.animalrescue.api.requests.GoogleIdTokenRequest;
import com.kkp.nure.animalrescue.api.requests.LoginRequest;
import com.kkp.nure.animalrescue.api.requests.MfaVerifyRequest;
import com.kkp.nure.animalrescue.api.requests.RegisterRequest;
import com.kkp.nure.animalrescue.api.responses.AuthResponse;
import com.kkp.nure.animalrescue.api.responses.ClientIdResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest body);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest body);

    @POST("auth/login/mfa")
    Call<AuthResponse> verifyMfa(@Body MfaVerifyRequest body);

    @GET("auth/google/mobile")
    Call<ClientIdResponse> getGoogleClientId();

    @POST("auth/google/mobile-callback")
    Call<AuthResponse> loginWithGoogleIdToken(@Body GoogleIdTokenRequest body);
}
