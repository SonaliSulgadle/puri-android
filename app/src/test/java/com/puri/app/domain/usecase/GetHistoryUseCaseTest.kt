package com.puri.app.domain.usecase

import com.puri.app.domain.model.Category
import com.puri.app.domain.model.ConfidenceLevel
import com.puri.app.domain.model.HistoryItem
import com.puri.app.domain.model.SolveResult
import com.puri.app.fake.FakeHistoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("GetHistoryUseCase")
class GetHistoryUseCaseTest {

    private lateinit var useCase: GetHistoryUseCase
    private lateinit var fakeHistoryRepo: FakeHistoryRepository

    private val fakeSolveResult = SolveResult(
        whatThisIs      = "Plastic Bin",
        description     = "Recyclable plastic container",
        steps           = emptyList(),
        warning         = null,
        koreaTip        = null,
        category        = Category.TRASH,
        confidenceLevel = ConfidenceLevel.HIGH,
        imageUri        = null,
        inputQuery      = null
    )

    @BeforeEach
    fun setUp() {
        fakeHistoryRepo = FakeHistoryRepository()
        useCase = GetHistoryUseCase(fakeHistoryRepo)
    }

    @Nested
    @DisplayName("given empty history")
    inner class EmptyHistory {

        @Test
        @DisplayName("emits empty list initially")
        fun emitsEmptyList() = runTest {
            val result = useCase().first()
            assertThat(result).isEmpty()
        }
    }

    @Nested
    @DisplayName("given items exist in history")
    inner class WithItems {

        @Test
        @DisplayName("emits all saved history items")
        fun emitsAllItems() = runTest {
            fakeHistoryRepo.saveToHistory(HistoryItem(id = 1L, solveResult = fakeSolveResult))
            fakeHistoryRepo.saveToHistory(HistoryItem(id = 2L, solveResult = fakeSolveResult))

            val result = useCase().first()

            assertThat(result).hasSize(2)
        }

        @Test
        @DisplayName("emits updated list when new item is saved")
        fun emitsUpdatedList() = runTest {
            val flow = useCase()

            fakeHistoryRepo.saveToHistory(HistoryItem(id = 1L, solveResult = fakeSolveResult))
            val result = flow.first()

            assertThat(result).hasSize(1)
            assertThat(result.first().solveResult.whatThisIs).isEqualTo("Plastic Bin")
        }
    }
}