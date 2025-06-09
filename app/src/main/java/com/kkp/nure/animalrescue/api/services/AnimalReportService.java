package com.kkp.nure.animalrescue.api.services;

import com.kkp.nure.animalrescue.api.requests.CreateAnimalReportRequest;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.entities.FoundReport;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AnimalReportService {
    @GET("animal-reports/recent")
    Call<PaginatedResponse<FoundReport>> getRecent(@Query("page") int page, @Query("page_size") int pageSize, @Query("lat") double latitude, @Query("lon") double longitude);

    @POST("animal-reports")
    Call<FoundReport> reportAnimal(@Body CreateAnimalReportRequest body);

    @POST("animal-reports/{reportId}/assign")
    Call<FoundReport> claimReport(@Path("reportId") long reportId);

    @GET("animal-reports/my")
    Call<PaginatedResponse<FoundReport>> getMy(@Query("page") int page, @Query("page_size") int pageSize);
}
