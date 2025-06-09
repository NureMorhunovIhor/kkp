package com.kkp.nure.animalrescue.api;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkp.nure.animalrescue.api.responses.ErrorResponse;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ApiClient {
    private static final String API_BASE = "https://kkp-api.ruslan.page/api/";
    private static Retrofit retrofit;
    private static Retrofit authRetrofit;
    private static String retrofitToken;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE)
                    .client(client)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getAuthClient(String token) {
        if (authRetrofit == null || !Objects.equals(retrofitToken, token)) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            retrofitToken = token;
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(token))
                    .addInterceptor(logging)
                    .build();

            authRetrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE)
                    .client(client)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return authRetrofit;
    }

    public static abstract class CustomCallback<T> implements Callback<T> {
        public abstract void onResponse(@NonNull T resp);
        public abstract void onError(@NonNull String error, int statusCode);

        public boolean onParseError(String body) {
            return false;
        }

        @Override
        public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
            if(response.isSuccessful() && (response.body() != null || response.code() == 204)) {
                onResponse(response.body());
                return;
            }

            ResponseBody errorBody = response.errorBody();
            if (errorBody == null) {
                onError("Failed to read response", response.code());
                return;
            }

            String errorString = null;

            try (errorBody) {
                errorString = errorBody.string();

                ErrorResponse errorResp = new ObjectMapper().readValue(errorString, ErrorResponse.class);
                if (errorResp.getErrors().isEmpty()) {
                    onError("Unknown error", response.code());
                    return;
                }
                onError(errorResp.getErrors().get(0), response.code());
            } catch (JacksonException e) {
                if(errorString != null) {
                    try {
                        if(onParseError(errorString))
                            return;
                        onError("Failed to parse response: "+e.getLocalizedMessage(), response.code());
                    } catch (Exception ne) {
                        onError("Failed to read response: "+ne.getLocalizedMessage(), response.code());
                    }
                } else {
                    onError("Failed to parse response: "+e.getLocalizedMessage(), response.code());
                }
            } catch (IOException e) {
                onError("Failed to read response: "+e.getLocalizedMessage(), response.code());
            }
        }

        @Override
        public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            onError(t.getLocalizedMessage(), 0);
        }
    }
}
