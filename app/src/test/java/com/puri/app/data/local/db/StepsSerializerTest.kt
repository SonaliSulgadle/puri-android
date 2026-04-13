package com.puri.app.data.local.db

import com.puri.app.domain.model.SolveStep
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("StepsSerializer")
class StepsSerializerTest {

    @Nested
    @DisplayName("toJson")
    inner class ToJson {

        @Test
        @DisplayName("empty list produces empty JSON array")
        fun emptyListProducesEmptyArray() {
            assertThat(StepsSerializer.toJson(emptyList())).isEqualTo("[]")
        }

        @Test
        @DisplayName("serializes order, title, description fields")
        fun serializesAllFields() {
            val json = StepsSerializer.toJson(
                listOf(SolveStep(1, "Rinse", "Remove residue"))
            )
            assertThat(json).contains("\"order\":1")
            assertThat(json).contains("\"title\":\"Rinse\"")
            assertThat(json).contains("\"description\":\"Remove residue\"")
        }
    }

    @Nested
    @DisplayName("fromJson")
    inner class FromJson {

        @Test
        @DisplayName("empty array string produces empty list")
        fun emptyArrayProducesEmptyList() {
            assertThat(StepsSerializer.fromJson("[]")).isEmpty()
        }

        @Test
        @DisplayName("blank string produces empty list")
        fun blankStringProducesEmptyList() {
            assertThat(StepsSerializer.fromJson("")).isEmpty()
        }

        @Test
        @DisplayName("malformed JSON produces empty list without crash")
        fun malformedJsonProducesEmptyList() {
            assertThat(StepsSerializer.fromJson("{not json}")).isEmpty()
        }

        @Test
        @DisplayName("missing title and description use empty string defaults")
        fun missingFieldsUseEmptyDefaults() {
            val result = StepsSerializer.fromJson("""[{"order":1}]""")
            assertThat(result).hasSize(1)
            assertThat(result[0].order).isEqualTo(1)
            assertThat(result[0].title).isEmpty()
            assertThat(result[0].description).isEmpty()
        }
    }

    @Nested
    @DisplayName("roundtrip")
    inner class Roundtrip {

        @Test
        @DisplayName("serialized then deserialized produces identical steps")
        fun roundtripPreservesData() {
            val original = listOf(
                SolveStep(1, "Step one", "Desc one"),
                SolveStep(2, "Step two", "Desc two"),
                SolveStep(3, "Step three", "Desc three")
            )
            val restored = StepsSerializer.fromJson(StepsSerializer.toJson(original))

            assertThat(restored).hasSize(3)
            original.forEachIndexed { i, step ->
                assertThat(restored[i].order).isEqualTo(step.order)
                assertThat(restored[i].title).isEqualTo(step.title)
                assertThat(restored[i].description).isEqualTo(step.description)
            }
        }
    }
}