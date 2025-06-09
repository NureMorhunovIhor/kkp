package com.kkp.nure.animalrescue.api.services;

import com.kkp.nure.animalrescue.api.requests.UpdateAnimalRequest;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.entities.Animal;
import com.kkp.nure.animalrescue.entities.FoundReport;
import com.kkp.nure.animalrescue.entities.TreatmentReport;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AnimalService {
    @GET("animals/{animalId}")
    Call<Animal> getAnimal(@Path("animalId") long animalId);

    @PATCH("animals/{animalId}")
    Call<Animal> updateAnimal(@Path("animalId") long animalId, @Body UpdateAnimalRequest body);

    @GET("animals/{animalId}/reports")
    Call<PaginatedResponse<FoundReport>> getFoundReports(@Path("animalId") long animalId, @Query("page") int page, @Query("page_size") int pageSize);

    @GET("animals/{animalId}/treatment-reports")
    Call<PaginatedResponse<TreatmentReport>> getTreatmentReports(@Path("animalId") long animalId, @Query("page") int page, @Query("page_size") int pageSize);
}
