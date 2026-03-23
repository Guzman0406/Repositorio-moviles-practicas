package com.autosync.main.data.remote.model

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<ChatMessage>,
    @SerializedName("max_tokens") val maxTokens: Int = 1000,
    val temperature: Double = 0.7
)

data class ChatMessage(
    val role: String,
    val content: String
)

data class ChatResponse(
    val choices: List<ChatChoice>? = null,
    val error: ChatError? = null
)

data class ChatChoice(
    val message: ChatMessage
)

data class ChatError(
    val message: String,
    val type: String?,
    val code: String?
)
