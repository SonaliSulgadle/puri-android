package com.puri.app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Request ───────────────────────────────────────────────────────────────

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerialName("generationConfig")
    val generationConfig: GenerationConfig = GenerationConfig()
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

@Serializable
data class GeminiPart(
    val text: String? = null,
    @SerialName("inline_data")
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    @SerialName("mime_type")
    val mimeType: String,
    val data: String  // base64 encoded
)

@Serializable
data class GenerationConfig(
    val temperature: Float = 0.2f,
    @SerialName("maxOutputTokens")
    val maxOutputTokens: Int = 512,
    val topK: Int = 40,
    val topP: Float = 0.95f
)

// ── Response ──────────────────────────────────────────────────────────────

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val error: GeminiApiError? = null
)

@Serializable
data class Candidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)

@Serializable
data class GeminiApiError(
    val code: Int = 0,
    val message: String = "",
    val status: String = ""
)