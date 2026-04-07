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

    private val validResponse = """
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

    @Nested
    @DisplayName("given a valid well-formed response")
    inner class ValidResponse {

        @Test
        @DisplayName("parses WHAT field correctly")
        fun parsesWhatField() {
            val result = parser.parse(validResponse, null, null)
            assertThat(result.whatThisIs).isEqualTo("LDPE Plastic Container")
        }

        @Test
        @DisplayName("parses DESCRIPTION field correctly")
        fun parsesDescription() {
            val result = parser.parse(validResponse, null, null)
            assertThat(result.description)
                .isEqualTo("Commonly used for food storage and squeeze bottles.")
        }

        @Test
        @DisplayName("parses all three steps correctly")
        fun parsesSteps() {
            val result = parser.parse(validResponse, null, null)
            assertThat(result.steps).hasSize(3)
            assertThat(result.steps.first().title).isEqualTo("Remove label")
            assertThat(result.steps.first().order).isEqualTo(1)
        }

        @Test
        @DisplayName("parses WARNING field correctly")
        fun parsesWarning() {
            val result = parser.parse(validResponse, null, null)
            assertThat(result.warning).isEqualTo("Do not mix with food waste")
        }

        @Test
        @DisplayName("parses TIP field correctly")
        fun parsesTip() {
            val result = parser.parse(validResponse, null, null)
            assertThat(result.koreaTip)
                .isEqualTo("Look for the recycling triangle with number 4")
        }

        @Test
        @DisplayName("parses CONFIDENCE as HIGH")
        fun parsesHighConfidence() {
            val result = parser.parse(validResponse, null, null)
            assertThat(result.confidenceLevel).isEqualTo(ConfidenceLevel.HIGH)
        }

        @Test
        @DisplayName("parses CATEGORY as TRASH")
        fun parsesCategory() {
            val result = parser.parse(validResponse, null, null)
            assertThat(result.category).isEqualTo(Category.TRASH)
        }
    }

    @Nested
    @DisplayName("given a low confidence response")
    inner class LowConfidenceResponse {

        private val lowConfidenceResponse = """
            WHAT: I could not identify this clearly.
            DESCRIPTION: The image is too blurry.
            STEPS:
            WARNING: NONE
            TIP: NONE
            CONFIDENCE: LOW
            CATEGORY: GENERAL
        """.trimIndent()

        @Test
        @DisplayName("returns LOW confidence level")
        fun returnsLowConfidence() {
            val result = parser.parse(lowConfidenceResponse, null, null)
            assertThat(result.confidenceLevel).isEqualTo(ConfidenceLevel.LOW)
        }

        @Test
        @DisplayName("returns empty steps list")
        fun returnsEmptySteps() {
            val result = parser.parse(lowConfidenceResponse, null, null)
            assertThat(result.steps).isEmpty()
        }

        @Test
        @DisplayName("returns null for NONE warning")
        fun returnsNullForNoneWarning() {
            val result = parser.parse(lowConfidenceResponse, null, null)
            assertThat(result.warning).isNull()
        }
    }

    @Nested
    @DisplayName("given a safety-triggered response")
    inner class SafetyResponse {

        private val safetyResponse = """
            WHAT: This requires professional help.
            DESCRIPTION: Medical or electrical issue detected.
            STEPS:
            WARNING: Please contact 119 (emergency) or a professional.
            TIP: NONE
            CONFIDENCE: LOW
            CATEGORY: GENERAL
        """.trimIndent()

        @Test
        @DisplayName("returns UNSAFE confidence level")
        fun returnsUnsafeConfidence() {
            val result = parser.parse(safetyResponse, null, null)
            assertThat(result.confidenceLevel).isEqualTo(ConfidenceLevel.UNSAFE)
        }
    }

    @Nested
    @DisplayName("given a malformed response")
    inner class MalformedResponse {

        @Test
        @DisplayName("defaults to LOW confidence on missing CONFIDENCE field")
        fun defaultsToLowOnMissingField() {
            val result = parser.parse("WHAT: Something\nDESCRIPTION: test", null, null)
            assertThat(result.confidenceLevel).isEqualTo(ConfidenceLevel.LOW)
        }

        @Test
        @DisplayName("defaults to GENERAL category on unknown category")
        fun defaultsToGeneral() {
            val result = parser.parse(
                "WHAT: X\nDESCRIPTION: Y\nCATEGORY: UNKNOWN_THING",
                null, null
            )
            assertThat(result.category).isEqualTo(Category.GENERAL)
        }
    }
}