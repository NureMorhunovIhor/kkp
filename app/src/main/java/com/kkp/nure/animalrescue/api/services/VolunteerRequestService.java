package com.kkp.nure.animalrescue.api.services;

import com.kkp.nure.animalrescue.api.requests.CreateVolunteerRequestRequest;
import com.kkp.nure.animalrescue.api.requests.Disable2faRequest;
import com.kkp.nure.animalrescue.api.requests.Enable2faRequest;
import com.kkp.nure.animalrescue.api.requests.RegisterFcmRequest;
import com.kkp.nure.animalrescue.api.requests.UpdateLocationRequest;
import com.kkp.nure.animalrescue.api.requests.UpdateUserInfoRequest;
import com.kkp.nure.animalrescue.entities.User;
import com.kkp.nure.animalrescue.entities.VolunteerRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface VolunteerRequestService {
    @GET("volunteer-requests")
    Call<List<VolunteerRequest>> getRequests();

    @POST("volunteer-requests")
    Call<VolunteerRequest> createRequest(@Body CreateVolunteerRequestRequest body);
}
