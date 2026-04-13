package com.puri.app.data.remote

import com.puri.app.domain.model.Category
import com.puri.app.domain.model.ConfidenceLevel
import com.puri.app.domain.model.SolveResult
import com.puri.app.domain.model.SolveStep
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiResponseParser @Inject constructor() {

    fun parse(rawResponse: String, imageUri: String?, inputQuery: String?): SolveResult {
        val lines = rawResponse.lines().map { it.trim() }.filter { it.isNotBlank() }

        val whatThisIs = extractField(lines, "WHAT") ?: ""
        val description = extractField(lines, "DESCRIPTION") ?: ""
        val warning = extractField(lines, "WARNING")
            ?.takeIf { it.uppercase() != "NONE" }
        val tip = extractField(lines, "TIP")
            ?.takeIf { it.uppercase() != "NONE" }
        val confidenceRaw = extractField(lines, "CONFIDENCE")?.uppercase()
        val categoryRaw = extractField(lines, "CATEGORY")?.uppercase()

        val confidence = when (confidenceRaw) {
            "HIGH" -> ConfidenceLevel.HIGH
            "LOW" -> ConfidenceLevel.LOW
            else -> ConfidenceLevel.LOW  // default to LOW on parse failure
        }

        // Check for safety triggers in WHAT or WARNING
        val isSafetyResponse = whatThisIs.contains("professional help", ignoreCase = true) ||
                warning?.contains("119", ignoreCase = true) == true
        val finalConfidence = if (isSafetyResponse) ConfidenceLevel.UNSAFE else confidence

        val steps = parseSteps(lines)
        val category = Category.fromString(categoryRaw ?: "GENERAL")

        return SolveResult(
            whatThisIs = whatThisIs,
            description = description,
            steps = steps,
            warning = warning,
            koreaTip = tip,
            category = category,
            confidenceLevel = finalConfidence,
            imageUri = imageUri,
            inputQuery = inputQuery
        )
    }

    private fun extractField(lines: List<String>, key: String): String? {
        return lines
            .firstOrNull {
                it.startsWith("$key:", ignoreCase = true) ||
                        it.startsWith("$key：", ignoreCase = true)
            }
            ?.removePrefix("$key:")
            ?.removePrefix("$key：")   // handle full-width colon in Korean responses
            ?.trim()
            ?.takeIf { it.isNotBlank() }
    }

    private fun parseSteps(lines: List<String>): List<SolveStep> {
        val steps = mutableListOf<SolveStep>()
        var inStepsBlock = false
        var order = 1

        for (line in lines) {
            when {
                line.startsWith("STEPS:", ignoreCase = true) -> {
                    inStepsBlock = true
                }

                inStepsBlock && line.matches(Regex("^\\d+\\..*")) -> {
                    // Format: "1. Action title | Description"
                    val content = line.removePrefix("${order}.").trim()
                    val parts = content.split("|", limit = 2)
                    val title = parts.getOrNull(0)?.trim() ?: content
                    val desc = parts.getOrNull(1)?.trim() ?: ""
                    steps.add(SolveStep(order = order, title = title, description = desc))
                    order++
                }

                inStepsBlock && (line.startsWith("WARNING:") || line.startsWith("TIP:")) -> {
                    inStepsBlock = false
                }
            }
        }
        return steps
    }
}