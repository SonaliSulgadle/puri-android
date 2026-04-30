package com.puri.app.data.remote

import android.graphics.Bitmap
import com.puri.app.BuildConfig
import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.core.common.toBase64
import com.puri.app.data.remote.model.GeminiContent
import com.puri.app.data.remote.model.GeminiPart
import com.puri.app.data.remote.model.GeminiRequest
import com.puri.app.data.remote.model.InlineData
import com.puri.app.data.remote.prompt.ImagePromptBuilder
import com.puri.app.data.remote.prompt.TextPromptBuilder
import com.puri.app.domain.model.AppLanguage
import com.puri.app.domain.model.SolveResult
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class GeminiDataSource @Inject constructor(
    private val api: GeminiApi,
    private val imagePromptBuilder: ImagePromptBuilder,
    private val textPromptBuilder: TextPromptBuilder,
    private val parser: GeminiResponseParser
) {
    companion object {
        private const val MAX_RETRIES = 3
        private const val BASE_DELAY_MS = 1000L
        private const val IMAGE_MIME_TYPE = "image/jpeg"
    }

    suspend fun solveImage(
        bitmap: Bitmap,
        imageUri: String?,
        additionalContext: String?,
        language: AppLanguage
    ): Resource<SolveResult> = withRetry {
        val prompt = imagePromptBuilder.build(additionalContext)
        val base64Image = bitmap.toBase64()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(
                            inlineData = InlineData(
                                mimeType = IMAGE_MIME_TYPE,
                                data = base64Image
                            )
                        ),
                        GeminiPart(text = prompt)
                    )
                )
            )
        )

        val response = api.generateContent(
            apiKey = BuildConfig.GEMINI_API_KEY,
            request = request
        )

        parseResponse(response, imageUri, null)
    }

    suspend fun solveText(
        query: String,
        language: AppLanguage
    ): Resource<SolveResult> = withRetry {
        val prompt = textPromptBuilder.build(query, language)

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = prompt))
                )
            )
        )

        val response = api.generateContent(
            apiKey = BuildConfig.GEMINI_API_KEY,
            request = request
        )

        parseResponse(response, null, query)
    }

    private fun parseResponse(
        response: retrofit2.Response<com.puri.app.data.remote.model.GeminiResponse>,
        imageUri: String?,
        inputQuery: String?
    ): Resource<SolveResult> {
        // HTTP-level error
        if (!response.isSuccessful) {
            return Resource.Error(
                when (response.code()) {
                    429 -> PuriError.ApiError(429)
                    503 -> PuriError.ApiError(503)
                    401 -> PuriError.ApiError(401)
                    else -> PuriError.ApiError(response.code())
                }
            )
        }

        val body = response.body()
            ?: return Resource.Error(PuriError.Unknown())

        // API-level error in response body
        body.error?.let { apiError ->
            if (apiError.code != 0) {
                return Resource.Error(PuriError.ApiError(apiError.code))
            }
        }

        // Extract text from first candidate
        val rawText = body.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: return Resource.Error(PuriError.Unknown())

        return Resource.Success(parser.parse(rawText, imageUri, inputQuery))
    }

    private suspend fun <T> withRetry(
        block: suspend () -> Resource<T>
    ): Resource<T> {
        var lastResult: Resource<T> = Resource.Error(PuriError.Unknown())

        repeat(MAX_RETRIES) { attempt ->
            lastResult = try {
                block()
            } catch (e: Exception) {
                Resource.Error(classifyException(e))
            }

            when {
                lastResult is Resource.Success -> return lastResult
                lastResult is Resource.Error -> {
                    val error = (lastResult as Resource.Error).error
                    if (error is PuriError.ApiError &&
                        (error.code == 429 || error.code == 503)
                    ) {
                        // Retryable — wait with exponential backoff
                        val delayMs = BASE_DELAY_MS * 2.0.pow(attempt).toLong()
                        delay(delayMs)
                        return@repeat // try again
                    } else {
                        return lastResult // not retryable
                    }
                }
            }
        }

        return lastResult
    }

    private fun classifyException(e: Exception): PuriError {
        val message = e.message?.lowercase() ?: ""
        return when {
            message.contains("quota") ||
                    message.contains("429") -> PuriError.ApiError(429)

            message.contains("503") ||
                    message.contains("unavailable") ||
                    message.contains("overloaded") -> PuriError.ApiError(503)

            message.contains("network") ||
                    message.contains("resolve") ||
                    message.contains("connect") -> PuriError.NoInternet

            else -> PuriError.Unknown(e)
        }
    }
}