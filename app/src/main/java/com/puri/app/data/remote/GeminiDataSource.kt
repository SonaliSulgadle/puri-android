package com.puri.app.data.remote

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.domain.model.AppLanguage
import com.puri.app.domain.model.SolveResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiDataSource @Inject constructor(
    private val model: GenerativeModel,
    private val promptBuilder: PromptBuilder,
    private val parser: GeminiResponseParser
) {
    suspend fun solveImage(
        bitmap: Bitmap,
        additionalContext: String?,
        imageUri: String?,
        language: AppLanguage
    ): Resource<SolveResult> = safeGeminiCall {
        val prompt = promptBuilder.buildImagePrompt(additionalContext, language)
        val response = model.generateContent(
            content {
                image(bitmap)
                text(prompt)
            }
        )
        val rawText = response.text
            ?: return@safeGeminiCall Resource.Error(PuriError.Unknown())

        val result = parser.parse(rawText, imageUri = imageUri, inputQuery = null)
        Resource.Success(result)
    }

    suspend fun solveText(
        query: String,
        language: AppLanguage
    ): Resource<SolveResult> = safeGeminiCall {
        val prompt = promptBuilder.buildTextPrompt(query, language)
        val response = model.generateContent(prompt)
        val rawText = response.text
            ?: return@safeGeminiCall Resource.Error(PuriError.Unknown())

        val result = parser.parse(rawText, imageUri = null, inputQuery = query)
        Resource.Success(result)
    }

    private suspend fun <T> safeGeminiCall(block: suspend () -> Resource<T>): Resource<T> =
        try {
            block()
        } catch (e: Exception) {
            when {
                e.message?.contains("network", ignoreCase = true) == true ->
                    Resource.Error(PuriError.NoInternet)

                e.message?.contains("quota", ignoreCase = true) == true ->
                    Resource.Error(PuriError.ApiError(429))

                else ->
                    Resource.Error(PuriError.Unknown(e))
            }
        }
}