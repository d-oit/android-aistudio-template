package com.example.template.ai

import com.example.template.BuildConfig

/**
 * Hybrid AI repository: tries Gemini cloud API first,
 * falls back to on-device heuristics if unavailable.
 */
class AiRepository(
    private val apiService: GeminiApiService = GeminiClient.service,
    private val apiKey: String = BuildConfig.GEMINI_API_KEY,
    private val model: String = GeminiConfig.MODEL_FLASH,
) {
    suspend fun analyze(prompt: String): AiResult {
        if (apiKey.isBlank()) return onDeviceFallback(prompt)
        return try {
            val response =
                apiService.generateContent(
                    model = model,
                    apiKey = apiKey,
                    request =
                        GeminiRequest(
                            contents = listOf(GeminiRequest.Content(parts = listOf(GeminiRequest.Part(prompt)))),
                        ),
                )
            AiResult.Cloud(response.text() ?: "No response")
        } catch (e: Exception) {
            onDeviceFallback(prompt)
        }
    }

    private fun onDeviceFallback(prompt: String): AiResult {
        // Replace with real on-device model (MediaPipe / ML Kit) as needed
        return AiResult.OnDevice("[On-device] Processed: ${prompt.take(80)}...")
    }
}

sealed class AiResult {
    data class Cloud(
        val text: String,
    ) : AiResult()

    data class OnDevice(
        val text: String,
    ) : AiResult()
}
