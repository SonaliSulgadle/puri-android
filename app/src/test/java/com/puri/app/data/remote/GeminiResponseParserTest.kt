package com.puri.app.data.remote

import com.puri.app.domain.model.Category
import com.puri.app.domain.model.ConfidenceLevel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("GeminiResponseParser")
class GeminiResponseParserTest {

    private lateinit var parser: GeminiResponseParser

    @BeforeEach
    fun setUp() {
        parser = GeminiResponseParser()
    }

    companion object {
        val VALID_RESPONSE = """
            WHAT: LDPE Plastic Container
            DESCRIPTION: Commonly used for food storage and squeeze bottles.
            STEPS:
            1. Remove label | Peel off any stickers
            2. Rinse | Remove all food residue
            3. Recyclable Bin | Place in the plastic container
            WARNING: Do not mix with food waste
            TIP: Look for the recycling triangle with number 4
            CONFIDENCE: HIGH
            CATEGORY: TRASH
        """.trimIndent()

        val LOW_CONFIDENCE_RESPONSE = """
            WHAT: I could not identify this clearly.
            DESCRIPTION: The image is unclear.
            STEPS:
            WARNING: NONE
            TIP: NONE
            CONFIDENCE: LOW
            CATEGORY: GENERAL
        """.trimIndent()

        val SAFETY_RESPONSE = """
            WHAT: This requires professional help.
            DESCRIPTION: Potential hazard detected.
            STEPS:
            WARNING: Contact 119 (emergency) or a professional.
            TIP: NONE
            CONFIDENCE: LOW
            CATEGORY: GENERAL
        """.trimIndent()

        val KOREAN_FULL_WIDTH_COLON = """
            WHAT：Korean Washing Machine
            DESCRIPTION：Standard front-load appliance.
            STEPS:
            1. Power | Press 전원 button
            WARNING：NONE
            TIP：NONE
            CONFIDENCE：HIGH
            CATEGORY：APPLIANCE
        """.trimIndent()
    }

    @Nested
    @DisplayName("valid response parsing")
    inner class ValidResponse {

        @Test
        @DisplayName("parses WHAT field correctly")
        fun parsesWhatField() {
            val result = parser.parse(VALID_RESPONSE, null, null)
            assertThat(result.whatThisIs).isEqualTo("LDPE Plastic Container")
        }

        @Test
        @DisplayName("parses DESCRIPTION field correctly")
        fun parsesDescription() {
            val result = parser.parse(VALID_RESPONSE, null, null)
            assertThat(result.description)
                .isEqualTo("Commonly used for food storage and squeeze bottles.")
        }

        @Test
        @DisplayName("parses correct number of steps")
        fun parsesStepCount() {
            val result = parser.parse(VALID_RESPONSE, null, null)
            assertThat(result.steps).hasSize(3)
        }

        @Test
        @DisplayName("parses step order correctly")
        fun parsesStepOrder() {
            val result = parser.parse(VALID_RESPONSE, null, null)
            assertThat(result.steps[0].order).isEqualTo(1)
            assertThat(result.steps[1].order).isEqualTo(2)
            assertThat(result.steps[2].order).isEqualTo(3)
        }

        @Test
        @DisplayName("parses step title and description separated by pipe")
        fun parsesStepPipeSeparation() {
            val result = parser.parse(VALID_RESPONSE, null, null)
            assertThat(result.steps[0].title).isEqualTo("Remove label")
            assertThat(result.steps[0].description).isEqualTo("Peel off any stickers")
        }

        @Test
        @DisplayName("parses WARNING field correctly")
        fun parsesWarning() {
            val result = parser.parse(VALID_RESPONSE, null, null)
            assertThat(result.warning).isEqualTo("Do not mix with food waste")
        }

        @Test
        @DisplayName("parses TIP field correctly")
        fun parsesTip() {
            val result = parser.parse(VALID_RESPONSE, null, null)
            assertThat(result.koreaTip)
                .isEqualTo("Look for the recycling triangle with number 4")
        }

        @Test
        @DisplayName("parses HIGH confidence")
        fun parsesHighConfidence() {
            val result = parser.parse(VALID_RESPONSE, null, null)
            assertThat(result.confidenceLevel).isEqualTo(ConfidenceLevel.HIGH)
        }

        @Test
        @DisplayName("parses TRASH category")
        fun parsesCategory() {
            val result = parser.parse(VALID_RESPONSE, null, null)
            assertThat(result.category).isEqualTo(Category.TRASH)
        }

        @Test
        @DisplayName("preserves imageUri passed in")
        fun preservesImageUri() {
            val uri = "content://media/external/images/1"
            val result = parser.parse(VALID_RESPONSE, uri, null)
            assertThat(result.imageUri).isEqualTo(uri)
        }

        @Test
        @DisplayName("preserves inputQuery passed in")
        fun preservesInputQuery() {
            val query = "How do I sort trash?"
            val result = parser.parse(VALID_RESPONSE, null, query)
            assertThat(result.inputQuery).isEqualTo(query)
        }
    }

    @Nested
    @DisplayName("NONE field handling")
    inner class NoneFields {

        @Test
        @DisplayName("WARNING: NONE returns null")
        fun warningNoneReturnsNull() {
            val result = parser.parse(LOW_CONFIDENCE_RESPONSE, null, null)
            assertThat(result.warning).isNull()
        }

        @Test
        @DisplayName("TIP: NONE returns null")
        fun tipNoneReturnsNull() {
            val result = parser.parse(LOW_CONFIDENCE_RESPONSE, null, null)
            assertThat(result.koreaTip).isNull()
        }
    }

    @Nested
    @DisplayName("low confidence response")
    inner class LowConfidenceResponse {

        @Test
        @DisplayName("parses LOW confidence level")
        fun parsesLowConfidence() {
            val result = parser.parse(LOW_CONFIDENCE_RESPONSE, null, null)
            assertThat(result.confidenceLevel).isEqualTo(ConfidenceLevel.LOW)
        }

        @Test
        @DisplayName("returns empty steps list")
        fun returnsEmptySteps() {
            val result = parser.parse(LOW_CONFIDENCE_RESPONSE, null, null)
            assertThat(result.steps).isEmpty()
        }
    }

    @Nested
    @DisplayName("safety response")
    inner class SafetyResponse {

        @Test
        @DisplayName("returns UNSAFE confidence when safety trigger detected")
        fun returnsUnsafeConfidence() {
            val result = parser.parse(SAFETY_RESPONSE, null, null)
            assertThat(result.confidenceLevel).isEqualTo(ConfidenceLevel.UNSAFE)
        }
    }

    @Nested
    @DisplayName("Korean full-width colon handling")
    inner class KoreanColon {

        @Test
        @DisplayName("parses fields with full-width Korean colon")
        fun parsesKoreanColon() {
            val result = parser.parse(KOREAN_FULL_WIDTH_COLON, null, null)
            assertThat(result.whatThisIs).isEqualTo("Korean Washing Machine")
            assertThat(result.confidenceLevel).isEqualTo(ConfidenceLevel.HIGH)
            assertThat(result.category).isEqualTo(Category.APPLIANCE)
        }
    }

    @Nested
    @DisplayName("malformed response handling")
    inner class MalformedResponse {

        @Test
        @DisplayName("empty string returns LOW confidence result")
        fun emptyStringReturnsLowConfidence() {
            val result = parser.parse("", null, null)
            assertThat(result.confidenceLevel).isEqualTo(ConfidenceLevel.LOW)
        }

        @Test
        @DisplayName("missing CONFIDENCE field defaults to LOW")
        fun missingConfidenceDefaultsToLow() {
            val response = "WHAT: Something\nDESCRIPTION: desc\nCATEGORY: GENERAL"
            val result = parser.parse(response, null, null)
            assertThat(result.confidenceLevel).isEqualTo(ConfidenceLevel.LOW)
        }

        @Test
        @DisplayName("unknown CATEGORY defaults to GENERAL")
        fun unknownCategoryDefaultsToGeneral() {
            val response = """
                WHAT: Something
                DESCRIPTION: desc
                CONFIDENCE: HIGH
                CATEGORY: UNDERWATER_BASKET_WEAVING
            """.trimIndent()
            val result = parser.parse(response, null, null)
            assertThat(result.category).isEqualTo(Category.GENERAL)
        }

        @Test
        @DisplayName("very short response returns LOW confidence")
        fun shortResponseReturnsLowConfidence() {
            val result = parser.parse("Hi", null, null)
            assertThat(result.confidenceLevel).isEqualTo(ConfidenceLevel.LOW)
        }

        @Test
        @DisplayName("returns all steps without truncation")
        fun returnsAllStepsWithoutTruncation() {
            val manySteps = buildString {
                appendLine("WHAT: Test")
                appendLine("DESCRIPTION: Test")
                appendLine("STEPS:")
                repeat(8) { i ->
                    appendLine("${i + 1}. Step ${i + 1} | Description ${i + 1}")
                }
                appendLine("WARNING: NONE")
                appendLine("TIP: NONE")
                appendLine("CONFIDENCE: HIGH")
                appendLine("CATEGORY: GENERAL")
            }
            val result = parser.parse(manySteps, null, null)
            assertThat(result.steps).hasSize(8) // all steps returned
        }
    }

    @Nested
    @DisplayName("category parsing")
    inner class CategoryParsing {

        @Test
        @DisplayName("parses all valid categories")
        fun parsesAllCategories() {
            val categories = mapOf(
                "TRASH" to Category.TRASH,
                "APPLIANCE" to Category.APPLIANCE,
                "TRANSPORT" to Category.TRANSPORT,
                "FOOD" to Category.FOOD,
                "MEDICAL" to Category.MEDICAL,
                "GENERAL" to Category.GENERAL
            )

            categories.forEach { (raw, expected) ->
                val response = """
                    WHAT: Test
                    DESCRIPTION: Test
                    CONFIDENCE: HIGH
                    CATEGORY: $raw
                """.trimIndent()
                val result = parser.parse(response, null, null)
                assertThat(result.category)
                    .describedAs("Category $raw should parse to $expected")
                    .isEqualTo(expected)
            }
        }

        @Test
        @DisplayName("category parsing is case-insensitive")
        fun categoryIsCaseInsensitive() {
            val response = """
                WHAT: Test
                DESCRIPTION: Test
                CONFIDENCE: HIGH
                CATEGORY: trash
            """.trimIndent()
            val result = parser.parse(response, null, null)
            assertThat(result.category).isEqualTo(Category.TRASH)
        }
    }
}