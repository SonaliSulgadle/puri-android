package com.puri.app.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.puri.app.data.remote.GeminiApi
import com.puri.app.data.remote.GeminiDataSource
import com.puri.app.data.remote.GeminiResponseParser
import com.puri.app.data.remote.prompt.ImagePromptBuilder
import com.puri.app.data.remote.prompt.TextPromptBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeminiModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (com.puri.app.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)  // Gemini can take time on large images
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()

    @Provides
    @Singleton
    fun provideGeminiApi(retrofit: Retrofit): GeminiApi =
        retrofit.create(GeminiApi::class.java)

    @Provides
    @Singleton
    fun provideGeminiResponseParser(): GeminiResponseParser = GeminiResponseParser()

    @Provides
    @Singleton
    fun provideGeminiDataSource(
        api: GeminiApi,
        imagePromptBuilder: ImagePromptBuilder,
        textPromptBuilder: TextPromptBuilder,
        parser: GeminiResponseParser
    ): GeminiDataSource = GeminiDataSource(api, imagePromptBuilder, textPromptBuilder, parser)
}