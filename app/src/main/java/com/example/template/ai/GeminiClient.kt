package com.example.template.ai

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiClient {
  private val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

  private val okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()

  val service: GeminiApiService =
    Retrofit.Builder()
      .baseUrl(GeminiConfig.BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(GeminiApiService::class.java)
}
