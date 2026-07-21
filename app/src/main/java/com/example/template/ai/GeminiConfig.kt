package com.example.template.ai

object GeminiConfig {
  const val MODEL_FLASH = "gemini-2.0-flash"
  const val MODEL_PRO = "gemini-2.5-pro"
  const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/"

  // Increase for complex tasks, reduce for cost efficiency
  const val MAX_OUTPUT_TOKENS = 8192
  const val TEMPERATURE = 0.7f
}
