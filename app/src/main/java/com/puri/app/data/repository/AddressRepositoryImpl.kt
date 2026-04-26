package com.puri.app.data.repository

import android.util.Log
import com.puri.app.BuildConfig
import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.data.remote.AddressResponseParser
import com.puri.app.data.remote.GeminiApi
import com.puri.app.data.remote.PromptBuilder
import com.puri.app.data.remote.model.GeminiContent
import com.puri.app.data.remote.model.GeminiPart
import com.puri.app.data.remote.model.GeminiRequest
import com.puri.app.domain.model.AddressResult
import com.puri.app.domain.repository.AddressRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepositoryImpl @Inject constructor(
    private val api: GeminiApi,
    private val promptBuilder: PromptBuilder,
    private val parser: AddressResponseParser
) : AddressRepository {

    override suspend fun convertAddress(rawAddress: String): Resource<AddressResult> {
        return withRetry(maxAttempts = 2) {
            val prompt = promptBuilder.buildAddressPrompt(rawAddress)

            val response = api.generateContent(
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = prompt))
                        )
                    )
                )
            )

            if (!response.isSuccessful) {
                return@withRetry Resource.Error(PuriError.ApiError(response.code()))
            }

            val rawText = response.body()
                ?.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?: return@withRetry Resource.Error(PuriError.Unknown())

            Log.d("AddressRepo", "Raw response: $rawText")

            val result = parser.parse(rawText, rawAddress)
                ?: return@withRetry Resource.Error(PuriError.Unknown())

            Resource.Success(result)
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