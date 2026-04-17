package com.puri.app.data.remote

import com.puri.app.domain.model.Category
import com.puri.app.domain.model.ConfidenceLevel
import com.puri.app.domain.model.SolveResult
import com.puri.app.domain.model.SolveStep
import com.puri.app.domain.model.VisibleTextItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiResponseParser @Inject constructor() {

    fun parse(
        rawResponse: String,
        imageUri: String?,
        inputQuery: String?
    ): SolveResult {
        if (rawResponse.length < 20) return createLowConfidenceResult(imageUri, inputQuery)

        val lines = rawResponse.lines().map { it.trim() }.filter { it.isNotBlank() }

        val whatThisIs = extractField(lines, "WHAT") ?: ""
        val description = extractField(lines, "DESCRIPTION") ?: ""
        val warning = extractField(lines, "WARNING")
            ?.takeIf { it.uppercase() != "NONE" }
        val tip = extractField(lines, "TIP")
            ?.takeIf { it.uppercase() != "NONE" }
        val recommendedAction = extractField(lines, "RECOMMENDED ACTION")
            ?.takeIf { it.uppercase() != "NONE" }
        val confidenceRaw = extractField(lines, "CONFIDENCE")?.uppercase()
        val categoryRaw = extractField(lines, "CATEGORY")?.uppercase()

        val confidence = when (confidenceRaw) {
            "HIGH" -> ConfidenceLevel.HIGH
            "LOW" -> ConfidenceLevel.LOW
            else -> ConfidenceLevel.LOW
        }

        val isSafetyResponse =
            whatThisIs.contains("professional help", ignoreCase = true) ||
                    warning?.contains("119", ignoreCase = true) == true
        val finalConfidence = if (isSafetyResponse) ConfidenceLevel.UNSAFE else confidence

        return SolveResult(
            whatThisIs = whatThisIs,
            description = description,
            steps = parseSteps(lines),
            visibleTexts = parseVisibleTexts(lines),
            recommendedAction = recommendedAction,
            warning = warning,
            koreaTip = tip,
            category = Category.fromString(categoryRaw ?: "GENERAL"),
            confidenceLevel = finalConfidence,
            imageUri = imageUri,
            inputQuery = inputQuery
        )
    }

    private fun extractField(lines: List<String>, key: String): String? =
        lines.firstOrNull { line ->
            line.startsWith("$key:", ignoreCase = true) ||
                    line.startsWith("$key：", ignoreCase = true)
        }?.let { line ->
            line.removePrefix("$key:")
                .removePrefix("$key：")
                .trim()
        }?.takeIf { it.isNotBlank() }

    private fun parseSteps(lines: List<String>): List<SolveStep> {
        val steps = mutableListOf<SolveStep>()
        var inStepsBlock = false
        var order = 1

        for (line in lines) {
            when {
                line.startsWith("STEPS:", ignoreCase = true) ||
                        line.startsWith("STEPS：", ignoreCase = true) -> {
                    inStepsBlock = true
                }

                inStepsBlock && line.matches(Regex("^\\d+\\..*")) -> {
                    val content = line.removePrefix("${order}.").trim()
                    val parts = content.split("|", limit = 2)
                    steps.add(
                        SolveStep(
                            order = order,
                            title = parts.getOrNull(0)?.trim() ?: content,
                            description = parts.getOrNull(1)?.trim() ?: ""
                        )
                    )
                    order++
                }

                inStepsBlock && isNewSection(line) -> inStepsBlock = false
            }
        }
        return steps
    }

    private fun parseVisibleTexts(lines: List<String>): List<VisibleTextItem> {
        val items = mutableListOf<VisibleTextItem>()
        var inBlock = false

        for (line in lines) {
            when {
                line.startsWith("VISIBLE TEXT:", ignoreCase = true) ||
                        line.startsWith("VISIBLE TEXT：", ignoreCase = true) -> {
                    inBlock = true
                }

                inBlock && isNewSection(line) -> inBlock = false
                inBlock && line.contains("→") -> {
                    val arrowParts = line.split("→", limit = 2)
                    if (arrowParts.size == 2) {
                        val original = arrowParts[0].trim().removePrefix("-").trim()
                        val rest = arrowParts[1]
                        val dashParts = rest.split("—", limit = 2)
                        val translation = dashParts[0].trim()
                        val explanation = dashParts.getOrNull(1)?.trim() ?: ""

                        // Skip if original and translation are identical (English→English)
                        // or if original has no Korean/non-Latin characters
                        val hasKorean = original.any {
                            it.code in 0xAC00..0xD7A3 ||
                                    it.code in 0x3040..0x30FF ||
                                    it.code in 0x4E00..0x9FFF
                        }
                        if (original.isNotBlank() &&
                            translation.isNotBlank() &&
                            (hasKorean || original.lowercase() != translation.lowercase())
                        ) {
                            items.add(VisibleTextItem(original, translation, explanation))
                        }
                    }
                }
            }
        }
        return items
    }

    private fun isNewSection(line: String): Boolean {
        val sectionPrefixes = listOf(
            "STEPS:", "STEPS：",
            "WARNING:", "WARNING：",
            "TIP:", "TIP：",
            "RECOMMENDED ACTION:", "RECOMMENDED ACTION：",
            "CONFIDENCE:", "CONFIDENCE：",
            "CATEGORY:", "CATEGORY："
        )
        return sectionPrefixes.any { line.startsWith(it, ignoreCase = true) }
    }

    private fun createLowConfidenceResult(
        imageUri: String?,
        inputQuery: String?
    ) = SolveResult(
        whatThisIs = "",
        description = "",
        steps = emptyList(),
        visibleTexts = emptyList(),
        recommendedAction = null,
        warning = null,
        koreaTip = null,
        category = Category.GENERAL,
        confidenceLevel = ConfidenceLevel.LOW,
        imageUri = imageUri,
        inputQuery = inputQuery
    )
}