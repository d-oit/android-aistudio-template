package com.example.template.ai

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig = GenerationConfig()
) {
    data class Content(val role: String = "user", val parts: List<Part>)
    data class Part(val text: String)
    data class GenerationConfig(
        val maxOutputTokens: Int = GeminiConfig.MAX_OUTPUT_TOKENS,
        val temperature: Float = GeminiConfig.TEMPERATURE
    )
}

data class GeminiResponse(
    val candidates: List<Candidate>?
) {
    data class Candidate(val content: Content?)
    data class Content(val parts: List<Part>?)
    data class Part(val text: String?)

    fun text(): String? = candidates
        ?.firstOrNull()?.content?.parts?.firstOrNull()?.text
}

interface GeminiApiService {
    @POST("models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
