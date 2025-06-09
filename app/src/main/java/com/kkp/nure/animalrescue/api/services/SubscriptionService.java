package com.kkp.nure.animalrescue.api.services;

import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.entities.Animal;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SubscriptionService {
    @GET("subscriptions")
    Call<PaginatedResponse<Animal>> getSubscriptions(@Query("page") int page, @Query("page_size") int pageSize);

    @PUT("subscriptions/{animalId}")
    Call<Void> subscribeToAnimal(@Path("animalId") long animalId);

    @DELETE("subscriptions/{animalId}")
    Call<Void> unsubscribeFromAnimal(@Path("animalId") long animalId);
}
