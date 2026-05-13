package com.puri.app.data.repository

import com.puri.app.BuildConfig
import com.puri.app.core.analytics.Crashlytics
import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.data.remote.AddressResponseParser
import com.puri.app.data.remote.GeminiApi
import com.puri.app.data.remote.model.GeminiContent
import com.puri.app.data.remote.model.GeminiPart
import com.puri.app.data.remote.model.GeminiRequest
import com.puri.app.data.remote.model.GenerationConfig
import com.puri.app.data.remote.prompt.AddressPromptBuilder
import com.puri.app.domain.model.AddressResult
import com.puri.app.domain.repository.AddressRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepositoryImpl @Inject constructor(
    private val api: GeminiApi,
    private val addressPromptBuilder: AddressPromptBuilder,
    private val parser: AddressResponseParser,
    private val crashlytics: Crashlytics
) : AddressRepository {

    override suspend fun convertAddress(rawAddress: String): Resource<AddressResult> {
        crashlytics.log("Address convert started")
        return withRetry(maxAttempts = 2) {
            try {

                val prompt = addressPromptBuilder.build(rawAddress)

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                    ),
                    generationConfig = GenerationConfig(
                        temperature = 0f,    // ← deterministic
                        maxOutputTokens = 300,
                        topP = 1f,
                        topK = 1      // greedy decoding
                    )
                )
                val response = api.generateContent(
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )

                if (!response.isSuccessful) {
                    crashlytics.recordApiError("convertAddress", response.code())
                    return@withRetry Resource.Error(PuriError.ApiError(response.code()))
                }

                val rawText = response.body()
                    ?.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text
                    ?: run {
                        crashlytics.recordException(
                            Exception("Null address response"),
                            "address_parse"
                        )
                        return@withRetry Resource.Error(PuriError.Unknown())
                    }

                val result = parser.parse(rawText, rawAddress)
                    ?: run {
                        crashlytics.log("Parser null for: ${rawAddress.take(50)}")
                        crashlytics.setKey("failed_address", rawAddress.take(100))
                        return@withRetry Resource.Error(PuriError.Unknown())
                    }

                Resource.Success(result)
            } catch (e: Exception) {
                crashlytics.recordException(e, "convertAddress")
                Resource.Error(PuriError.Unknown(e))
            }
        }
    }

    private suspend fun <T> withRetry(
        maxAttempts: Int,
        block: suspend () -> Resource<T>
    ): Resource<T> {
        var lastResult: Resource<T> = Resource.Error(PuriError.Unknown())
        repeat(maxAttempts) { attempt ->
            lastResult = try {
                block()
            } catch (e: Exception) {
                Resource.Error(PuriError.Unknown(e))
            }
            if (lastResult is Resource.Success) return lastResult
            if (attempt < maxAttempts - 1) delay(1000L * (attempt + 1))
        }
        return lastResult
    }
}