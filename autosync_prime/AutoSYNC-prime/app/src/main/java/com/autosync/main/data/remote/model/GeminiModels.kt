package com.autosync.main.data.remote.model

import com.google.gson.annotations.SerializedName

// --- Chat Models ---
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null
)

data class Content(
    val parts: List<Part>,
    val role: String? = null
)

data class Part(
    val text: String
)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    val topK: Int = 32,
    val topP: Float = 1.0f,
    val maxOutputTokens: Int = 1024
)

data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val error: GeminiError? = null
)

data class Candidate(
    val content: Content,
    val finishReason: String?,
    val index: Int?,
    val safetyRatings: List<SafetyRating>?
)

data class SafetyRating(
    val category: String,
    val probability: String
)

data class GeminiError(
    val code: Int,
    val message: String,
    val status: String
)

// --- Model List Models ---
data class ModelListResponse(
    val models: List<GeminiModel>? = null,
    val error: GeminiError? = null
)

data class GeminiModel(
    val name: String, // e.g. "models/gemini-1.5-flash"
    val displayName: String?,
    val supportedGenerationMethods: List<String>?
)
