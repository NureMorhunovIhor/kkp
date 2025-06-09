package com.kkp.nure.animalrescue.api.services;

import com.kkp.nure.animalrescue.api.requests.CreateDonationRequest;
import com.kkp.nure.animalrescue.api.requests.SendMessageRequest;
import com.kkp.nure.animalrescue.api.responses.DonationCreatedResponse;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.entities.Dialog;
import com.kkp.nure.animalrescue.entities.Donation;
import com.kkp.nure.animalrescue.entities.DonationGoal;
import com.kkp.nure.animalrescue.entities.Message;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DonationService {
    @GET("donations")
    Call<PaginatedResponse<DonationGoal>> getGoals(@Query("page") int page, @Query("page_size") int pageSize);

    @GET("donations/{goalId}")
    Call<DonationGoal> getGoal(@Path("goalId") long goalId);

    @GET("donations/{goalId}/donations")
    Call<PaginatedResponse<Donation>> getGoalDonations(@Path("goalId") long goalId, @Query("page") int page, @Query("page_size") int pageSize);

    @POST("donations/{goalId}/donate")
    Call<DonationCreatedResponse> createDonation(@Path("goalId") long goalId, @Body CreateDonationRequest body);

    @POST("donations/{goalId}/donations/{donationId}")
    Call<Donation> finishPayment(@Path("goalId") long goalId, @Path("donationId") long donationId);
}
