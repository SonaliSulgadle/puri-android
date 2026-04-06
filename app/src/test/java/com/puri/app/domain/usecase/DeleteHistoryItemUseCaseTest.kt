package com.puri.app.domain.usecase

import com.puri.app.core.common.Resource
import com.puri.app.domain.model.HistoryItem
import com.puri.app.fake.FakeHistoryRepository
import com.puri.app.util.TestFixtures.applianceSolveResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("DeleteHistoryItemUseCase")
class DeleteHistoryItemUseCaseTest {

    private lateinit var useCase: DeleteHistoryItemUseCase
    private lateinit var fakeHistoryRepo: FakeHistoryRepository

    @BeforeEach
    fun setUp() {
        fakeHistoryRepo = FakeHistoryRepository()
        useCase = DeleteHistoryItemUseCase(fakeHistoryRepo)
    }

    @Nested
    @DisplayName("given item exists in history")
    inner class ItemExists {

        @Test
        @DisplayName("returns Success after deletion")
        fun returnsSuccess() = runTest {
            fakeHistoryRepo.saveToHistory(HistoryItem(id = 1L, solveResult = applianceSolveResult))

            val result = useCase(1L)

            assertThat(result).isInstanceOf(Resource.Success::class.java)
        }

        @Test
        @DisplayName("item is no longer in history after deletion")
        fun itemRemovedFromHistory() = runTest {
            fakeHistoryRepo.saveToHistory(HistoryItem(id = 1L, solveResult = applianceSolveResult))

            useCase(1L)

            val history = fakeHistoryRepo.getHistory().first()
            assertThat(history).isEmpty()
        }

        @Test
        @DisplayName("only deletes the specified item, not others")
        fun deletesOnlySpecifiedItem() = runTest {
            fakeHistoryRepo.saveToHistory(HistoryItem(id = 1L, solveResult = applianceSolveResult))
            fakeHistoryRepo.saveToHistory(HistoryItem(id = 2L, solveResult = applianceSolveResult))

            useCase(1L)

            val history = fakeHistoryRepo.getHistory().first()
            assertThat(history).hasSize(1)
            assertThat(history.first().id).isEqualTo(2L)
        }
    }

    @Nested
    @DisplayName("given item does not exist")
    inner class ItemNotFound {

        @Test
        @DisplayName("returns Success even for non-existent id")
        fun returnsSuccessForMissingId() = runTest {
            // Delete on a non-existent item is a no-op — not an error
            val result = useCase(999L)
            assertThat(result).isInstanceOf(Resource.Success::class.java)
        }
    }
}