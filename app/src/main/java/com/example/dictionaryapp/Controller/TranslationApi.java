package com.example.dictionaryapp.Controller;

import com.example.dictionaryapp.Entity.TranslationRequest;
import com.example.dictionaryapp.Entity.TranslationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface TranslationApi {
    @POST("v2")
    Call<TranslationResponse> translateText(
            @Header("Content-Type") String contentType,
            @Header("Accept") String accept,
            @Header("X-RapidAPI-Key") String apiKey,
            @Header("X-RapidAPI-Host") String apiHost,
            @Body TranslationRequest request
    );
}