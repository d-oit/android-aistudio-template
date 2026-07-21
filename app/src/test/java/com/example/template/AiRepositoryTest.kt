package com.example.template

import com.example.template.ai.AiResult
import com.example.template.ai.AiRepository
import com.example.template.ai.GeminiApiService
import com.example.template.ai.GeminiConfig
import com.example.template.ai.GeminiRequest
import com.example.template.ai.GeminiResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AiRepositoryTest {

    @Test
    fun `analyze returns OnDevice when API key is blank`() {
        val repository = AiRepository(
            apiService = FakeGeminiApiService(),
            apiKey = "",
            model = GeminiConfig.MODEL_FLASH,
        )

        val result = repository.analyze("test prompt")

        assertTrue("Expected OnDevice result", result is AiResult.OnDevice)
        assertTrue(
            "Expected text to contain prompt",
            (result as AiResult.OnDevice).text.contains("test prompt"),
        )
    }

    @Test
    fun `analyze returns Cloud on successful API response`() {
        val fakeService = FakeGeminiApiService(
            responseText = "AI generated response",
        )
        val repository = AiRepository(
            apiService = fakeService,
            apiKey = "valid-api-key",
            model = GeminiConfig.MODEL_FLASH,
        )

        val result = repository.analyze("summarize this")

        assertTrue("Expected Cloud result", result is AiResult.Cloud)
        assertEquals(
            "AI generated response",
            (result as AiResult.Cloud).text,
        )
    }

    @Test
    fun `analyze falls back to OnDevice when API throws exception`() {
        val fakeService = FakeGeminiApiService(shouldThrow = true)
        val repository = AiRepository(
            apiService = fakeService,
            apiKey = "valid-api-key",
            model = GeminiConfig.MODEL_FLASH,
        )

        val result = repository.analyze("test prompt")

        assertTrue("Expected OnDevice fallback", result is AiResult.OnDevice)
    }

    @Test
    fun `geminiConfig has correct base url`() {
        assertEquals(
            "https://generativelanguage.googleapis.com/v1beta/",
            GeminiConfig.BASE_URL,
        )
    }
}

/**
 * Fake [GeminiApiService] for unit testing.
 */
private class FakeGeminiApiService(
    private val responseText: String = "Default response",
    private val shouldThrow: Boolean = false,
) : GeminiApiService {

    override suspend fun generateContent(
        model: String,
        apiKey: String,
        request: GeminiRequest,
    ): GeminiResponse {
        if (shouldThrow) throw RuntimeException("Network error")
        return GeminiResponse(
            candidates = listOf(
                GeminiResponse.Candidate(
                    content = GeminiResponse.Content(
                        parts = listOf(GeminiResponse.Part(text = responseText)),
                    ),
                ),
            ),
        )
    }
}
