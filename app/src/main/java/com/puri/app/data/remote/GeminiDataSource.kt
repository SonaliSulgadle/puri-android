package com.puri.app.data.remote

import android.graphics.Bitmap
import com.puri.app.BuildConfig
import com.puri.app.core.analytics.Crashlytics
import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.core.common.toBase64
import com.puri.app.data.remote.model.GeminiContent
import com.puri.app.data.remote.model.GeminiPart
import com.puri.app.data.remote.model.GeminiRequest
import com.puri.app.data.remote.model.GeminiResponse
import com.puri.app.data.remote.model.InlineData
import com.puri.app.data.remote.prompt.ImagePromptBuilder
import com.puri.app.data.remote.prompt.TextPromptBuilder
import com.puri.app.domain.model.AppLanguage
import com.puri.app.domain.model.SolveResult
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class GeminiDataSource @Inject constructor(
    private val api: GeminiApi,
    private val imagePromptBuilder: ImagePromptBuilder,
    private val textPromptBuilder: TextPromptBuilder,
    private val parser: GeminiResponseParser,
    private val crashlytics: Crashlytics
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
        crashlytics.log("Image solve started — has_context=${additionalContext != null}")
        crashlytics.setKey("current_operation", "image_solve")

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
        crashlytics.log("Text solve started — query_length=${query.length}")
        crashlytics.setKey("current_operation", "text_solve")

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
        response: Response<GeminiResponse>,
        imageUri: String?,
        inputQuery: String?
    ): Resource<SolveResult> {
        // HTTP-level error
        if (!response.isSuccessful) {
            crashlytics.recordApiError(
                endpoint = if (imageUri != null) "generateContent/image" else "generateContent/text",
                httpCode = response.code(),
                body = response.errorBody()?.string()?.take(200)
            )
            return Resource.Error(
                when (response.code()) {
                    429 -> PuriError.ApiError(429)
                    503 -> PuriError.ApiError(503)
                    401 -> PuriError.ApiError(401)
                    else -> PuriError.ApiError(response.code())
                }
            )
        }

        val body = response.body() ?: run {
            crashlytics.recordException(
                Exception("Null response body despite HTTP 200"),
                "gemini_parse"
            )
            return Resource.Error(PuriError.Unknown())
        }

        // API-level error in response body
        body.error?.let { apiError ->
            if (apiError.code != 0) {
                crashlytics.recordApiError(
                    endpoint = "generateContent/body_error",
                    httpCode = apiError.code,
                    body = apiError.message
                )
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
            ?: run {
                crashlytics.recordException(
                    Exception("No text content in Gemini response candidates"),
                    "gemini_parse_no_text"
                )
                return Resource.Error(PuriError.Unknown())
            }

        return try {
            val result = parser.parse(rawText, imageUri, inputQuery)
            crashlytics.log("Parse successful — category=${result.category}")
            Resource.Success(result)
        } catch (e: Exception) {
            // Parser threw — log raw text prefix for debugging
            crashlytics.setKey("failed_raw_response", rawText.take(200))
            crashlytics.recordException(e, "gemini_response_parse")
            Resource.Error(PuriError.Unknown(e))
        }
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
                        crashlytics.log("Retrying after ${error.code} — attempt=${attempt + 1}, delay=${delayMs}ms")
                        delay(delayMs)
                        return@repeat // try again
                    } else {
                        return lastResult // not retryable
                    }
                }
            }
        }

        crashlytics.log("All $MAX_RETRIES retries exhausted")
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

            e is java.net.SocketTimeoutException ||
                    message.contains("timeout") ||
                    message.contains("timed out") -> PuriError.Timeout

            else -> PuriError.Unknown(e)
        }
    }
}