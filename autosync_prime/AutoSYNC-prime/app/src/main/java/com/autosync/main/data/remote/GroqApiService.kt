package com.autosync.main.data.remote

import com.autosync.main.data.remote.model.ChatRequest
import com.autosync.main.data.remote.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Standard OpenAI Chat Completion Interface
interface GroqApiService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun chatCompletions(
        @retrofit2.http.Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): ChatResponse
}
