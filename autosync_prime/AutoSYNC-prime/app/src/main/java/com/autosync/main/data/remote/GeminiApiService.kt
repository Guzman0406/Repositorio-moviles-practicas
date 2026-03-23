package com.autosync.main.data.remote

import com.autosync.main.data.remote.model.GeminiRequest
import com.autosync.main.data.remote.model.GeminiResponse
import com.autosync.main.data.remote.model.ModelListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface GeminiApiService {
    @GET("v1beta/models")
    suspend fun listModels(
        @Query("key") apiKey: String
    ): ModelListResponse

    @POST
    suspend fun generateContent(
        @Url url: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
