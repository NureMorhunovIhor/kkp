package com.kkp.nure.animalrescue.api.services;

import com.kkp.nure.animalrescue.api.requests.SendMessageRequest;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.entities.Dialog;
import com.kkp.nure.animalrescue.entities.Message;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageService {
    @GET("messages")
    Call<PaginatedResponse<Dialog>> getDialogs(@Query("page") int page, @Query("page_size") int pageSize);

    @GET("messages/{userId}")
    Call<PaginatedResponse<Message>> getMessages(@Path("userId") long userId, @Query("before_id") Long beforeId, @Query("limit") int limit);

    @POST("messages/{userId}")
    Call<Message> sendMessage(@Path("userId") long userId, @Body SendMessageRequest body);
}
