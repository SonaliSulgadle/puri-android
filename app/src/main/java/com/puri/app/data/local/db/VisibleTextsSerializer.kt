package com.puri.app.data.local.db

import com.puri.app.domain.model.VisibleTextItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object VisibleTextsSerializer {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Serializable
    private data class VisibleTextDto(
        val original: String = "",
        val translation: String = "",
        val explanation: String = ""
    )

    fun toJson(items: List<VisibleTextItem>): String {
        if (items.isEmpty()) return "[]"
        return json.encodeToString(
            items.map { VisibleTextDto(it.original, it.translation, it.explanation) }
        )
    }

    fun fromJson(jsonString: String): List<VisibleTextItem> {
        if (jsonString.isBlank() || jsonString == "[]") return emptyList()
        return try {
            json.decodeFromString<List<VisibleTextDto>>(jsonString)
                .map { VisibleTextItem(it.original, it.translation, it.explanation) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}