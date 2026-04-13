package com.puri.app.data.local.db

import com.puri.app.domain.model.SolveStep
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object StepsSerializer {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Serializable
    private data class SolveStepDto(
        val order: Int = 0,
        val title: String = "",
        val description: String = ""
    )

    fun toJson(steps: List<SolveStep>): String {
        if (steps.isEmpty()) return "[]"
        val dtos = steps.map { SolveStepDto(it.order, it.title, it.description) }
        return json.encodeToString(dtos)
    }

    fun fromJson(jsonString: String): List<SolveStep> {
        if (jsonString.isBlank() || jsonString == "[]") return emptyList()
        return try {
            json.decodeFromString<List<SolveStepDto>>(jsonString)
                .map { SolveStep(it.order, it.title, it.description) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}