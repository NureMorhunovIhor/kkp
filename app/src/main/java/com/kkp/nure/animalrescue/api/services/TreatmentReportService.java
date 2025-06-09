package com.kkp.nure.animalrescue.api.services;

import com.kkp.nure.animalrescue.api.requests.CreateAnimalReportRequest;
import com.kkp.nure.animalrescue.api.requests.CreateTreatmentReportRequest;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.entities.FoundReport;
import com.kkp.nure.animalrescue.entities.TreatmentReport;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TreatmentReportService {
    @POST("treatment-reports")
    Call<TreatmentReport> createTreatmentReport(@Body CreateTreatmentReportRequest body);
}
