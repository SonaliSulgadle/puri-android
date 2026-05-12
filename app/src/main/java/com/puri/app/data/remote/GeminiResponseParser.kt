// GeminiResponseParser.kt — complete updated file

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

    companion object {
        // All known section header prefixes — used to detect where one field ends
        private val SECTION_PREFIXES = listOf(
            "WHAT:", "DESCRIPTION:", "ANSWER:", "VISIBLE TEXT:",
            "STEPS:", "WARNING:", "TIP:", "RECOMMENDED ACTION:",
            "CONFIDENCE:", "CATEGORY:",
            // Full-width colon variants
            "WHAT：", "DESCRIPTION：", "ANSWER：", "VISIBLE TEXT：",
            "STEPS：", "WARNING：", "TIP：", "RECOMMENDED ACTION：",
            "CONFIDENCE：", "CATEGORY："
        )
    }

    fun parse(
        rawResponse: String,
        imageUri: String?,
        inputQuery: String?
    ): SolveResult {
        if (rawResponse.length < 20) return createLowConfidenceResult(imageUri, inputQuery)

        val lines = rawResponse.lines().map { it.trim() }.filter { it.isNotBlank() }

        val hasSteps = lines.any {
            it.startsWith("STEPS:", ignoreCase = true) ||
                    it.startsWith("STEPS：", ignoreCase = true) ||
                    it.matches(Regex("^\\d+\\..*"))
        }
        val hasAnswer = lines.any {
            it.startsWith("ANSWER:", ignoreCase = true) ||
                    it.startsWith("ANSWER：", ignoreCase = true)
        }

        return if (hasAnswer && !hasSteps) {
            parseSimpleFormat(lines, imageUri, inputQuery)
        } else {
            parseProcessFormat(lines, imageUri, inputQuery)
        }
    }

    private fun parseProcessFormat(
        lines: List<String>,
        imageUri: String?,
        inputQuery: String?
    ): SolveResult {
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
        val finalConfidence = extractConfidence(confidenceRaw, whatThisIs, warning)

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

    private fun parseSimpleFormat(
        lines: List<String>,
        imageUri: String?,
        inputQuery: String?
    ): SolveResult {
        val whatThisIs = extractField(lines, "WHAT") ?: ""
        // extractField now collects ALL lines belonging to ANSWER
        // including multi-line responses like translations with multiple phrases
        val answer = extractField(lines, "ANSWER") ?: ""
        val warning = extractField(lines, "WARNING")
            ?.takeIf { it.uppercase() != "NONE" }
        val tip = extractField(lines, "TIP")
            ?.takeIf { it.uppercase() != "NONE" }
        val confidenceRaw = extractField(lines, "CONFIDENCE")?.uppercase()
        val categoryRaw = extractField(lines, "CATEGORY")?.uppercase()
        val finalConfidence = extractConfidence(confidenceRaw, whatThisIs, warning)

        return SolveResult(
            whatThisIs = whatThisIs,
            description = answer,
            steps = emptyList(),
            warning = warning,
            koreaTip = tip,
            recommendedAction = null,
            visibleTexts = emptyList(),
            confidenceLevel = finalConfidence,
            category = Category.fromString(categoryRaw ?: "GENERAL"),
            imageUri = imageUri,
            inputQuery = inputQuery
        )
    }

    private fun extractConfidence(
        confidenceRaw: String?,
        whatThisIs: String,
        warning: String?
    ): ConfidenceLevel {
        val confidence = when (confidenceRaw) {
            "HIGH" -> ConfidenceLevel.HIGH
            "LOW" -> ConfidenceLevel.LOW
            else -> ConfidenceLevel.LOW
        }
        val isSafetyResponse =
            whatThisIs.contains("professional help", ignoreCase = true) ||
                    warning?.contains("119", ignoreCase = true) == true
        return if (isSafetyResponse) ConfidenceLevel.UNSAFE else confidence
    }

    private fun extractField(lines: List<String>, key: String): String? {
        // Find the line that starts with this key
        val startIndex = lines.indexOfFirst { line ->
            line.startsWith("$key:", ignoreCase = true) ||
                    line.startsWith("$key：", ignoreCase = true)
        }
        if (startIndex == -1) return null

        // Extract value on the same line as the key
        val headerLine = lines[startIndex]
        val firstValue = headerLine
            .removePrefix("$key:")
            .removePrefix("$key：")
            .trim()

        // Collect continuation lines until we hit another section header
        val sb = StringBuilder()
        if (firstValue.isNotBlank()) {
            sb.append(firstValue)
        }

        for (i in startIndex + 1 until lines.size) {
            val line = lines[i]

            // Stop if we hit any known section header
            if (isSectionHeader(line)) break

            // Stop at numbered steps if we're not collecting STEPS field
            if (key != "STEPS" && line.matches(Regex("^\\d+\\..*"))) break

            // Add continuation line
            if (sb.isNotEmpty()) sb.append("\n")
            sb.append(line)
        }

        return sb.toString().trim().takeIf { it.isNotBlank() }
    }

    private fun isSectionHeader(line: String): Boolean =
        SECTION_PREFIXES.any { prefix ->
            line.startsWith(prefix, ignoreCase = true)
        }

    private fun isNewSection(line: String): Boolean = isSectionHeader(line)

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
                    // Match any digit prefix, not just sequential order
                    // Handles cases where AI skips numbers or restarts
                    val content = line.replaceFirst(Regex("^\\d+\\.\\s*"), "")
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
                        // Handle both — (em dash) and - (hyphen) as separators
                        val dashParts = rest.split("—", "-", limit = 2)
                        val translation = dashParts[0].trim()
                        val explanation = dashParts.getOrNull(1)?.trim() ?: ""

                        val hasKorean = original.any {
                            it.code in 0xAC00..0xD7A3 ||  // Hangul syllables
                                    it.code in 0x3040..0x30FF ||  // Hiragana/Katakana
                                    it.code in 0x4E00..0x9FFF     // CJK unified
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