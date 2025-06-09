package com.kkp.nure.animalrescue.api.services;

import androidx.annotation.NonNull;

import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.requests.UploadMediaRequest;
import com.kkp.nure.animalrescue.api.responses.UploadMediaResponse;
import com.kkp.nure.animalrescue.entities.Media;
import com.kkp.nure.animalrescue.enums.MediaType;

import java.io.IOException;

import lombok.AllArgsConstructor;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MediaService {
    @POST("media")
    Call<UploadMediaResponse> createUpload(@Body UploadMediaRequest body);

    @POST("media/{mediaId}/finalize")
    Call<Media> finalizeUpload(@Path("mediaId") long mediaId);

    @AllArgsConstructor
    public class Uploader {
        private MediaService service;

        public void uploadPhoto(byte[] data, PhotoUploadedCallback callback) {
            service.createUpload(new UploadMediaRequest(MediaType.PHOTO, data.length)).enqueue(new ApiClient.CustomCallback<>() {
                @Override
                public void onResponse(@NonNull UploadMediaResponse response) {
                    OkHttpClient client = new OkHttpClient();
                    long mediaId = response.getId();

                    RequestBody body = RequestBody.create(okhttp3.MediaType.get("application/octet-stream"), data);
                    Request request = new Request.Builder()
                            .url(response.getUploadUrl())
                            .put(body)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) {
                            if(!response.isSuccessful()) {
                                return;
                            }

                            service.finalizeUpload(mediaId).enqueue(new ApiClient.CustomCallback<>() {
                                @Override
                                public void onResponse(@NonNull Media response) {
                                    callback.onSuccess(response);
                                }

                                @Override
                                public void onError(@NonNull String error, int code) {
                                    callback.onError(error);
                                }
                            });
                        }

                        @Override
                        public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                            callback.onError("Failed to upload media.");
                        }
                    });
                }

                @Override
                public void onError(@NonNull String error, int code) {
                    callback.onError(error);
                }
            });
        }
    }

    public interface PhotoUploadedCallback {
        public void onSuccess(Media media);
        public void onError(String error);
    }
}
