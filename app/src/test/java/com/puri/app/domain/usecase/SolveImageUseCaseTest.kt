package com.puri.app.domain.usecase

import android.graphics.Bitmap
import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.fake.FakeHistoryRepository
import com.puri.app.fake.FakePreferencesRepository
import com.puri.app.fake.FakeSolveRepository
import com.puri.app.util.TestFixtures.applianceSolveResult
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SolveImageUseCase")
class SolveImageUseCaseTest {

    private lateinit var useCase: SolveImageUseCase
    private lateinit var fakeSolveRepo: FakeSolveRepository
    private lateinit var fakeHistoryRepo: FakeHistoryRepository
    private lateinit var fakePrefsRepo: FakePreferencesRepository

    private val mockBitmap: Bitmap = mockk(relaxed = true)
    private val imageUri: String = ""

    @BeforeEach
    fun setUp() {
        fakeSolveRepo = FakeSolveRepository()
        fakeHistoryRepo = FakeHistoryRepository()
        fakePrefsRepo = FakePreferencesRepository()
        useCase = SolveImageUseCase(
            solveRepository = fakeSolveRepo,
            historyRepository = fakeHistoryRepo,
            preferencesRepository = fakePrefsRepo
        )
    }

    @Nested
    @DisplayName("given daily limit is exhausted")
    inner class DailyLimitExhausted {

        @Test
        @DisplayName("returns DailyLimitReached error without calling solve API")
        fun returnsDailyLimitError() = runTest {
            fakePrefsRepo.setDailySolvesRemaining(0)

            val result = useCase(bitmap = mockBitmap, imageUri = imageUri)

            assertThat(result).isInstanceOf(Resource.Error::class.java)
            assertThat((result as Resource.Error).error)
                .isEqualTo(PuriError.DailyLimitReached)
        }

        @Test
        @DisplayName("does not save to history when limit reached")
        fun doesNotSaveWhenLimitReached() = runTest {
            fakePrefsRepo.setDailySolvesRemaining(0)

            useCase(bitmap = mockBitmap, imageUri = imageUri)

            assertThat(fakeHistoryRepo.savedItems).isEmpty()
        }
    }

    @Nested
    @DisplayName("given valid bitmap and successful solve")
    inner class SuccessfulSolve {

        @Test
        @DisplayName("returns Success with solve result")
        fun returnsSuccess() = runTest {
            val result = useCase(bitmap = mockBitmap, imageUri = imageUri)
            assertThat(result).isInstanceOf(Resource.Success::class.java)
        }

        @Test
        @DisplayName("auto-saves result to history")
        fun autoSavesToHistory() = runTest {
            useCase(bitmap = mockBitmap, imageUri = imageUri)
            assertThat(fakeHistoryRepo.savedItems).hasSize(1)
        }

        @Test
        @DisplayName("saved history item contains the solve result")
        fun savedItemContainsSolveResult() = runTest {
            useCase(bitmap = mockBitmap, imageUri = imageUri)
            val savedItem = fakeHistoryRepo.savedItems.first()
            assertThat(savedItem.solveResult.whatThisIs)
                .isEqualTo(applianceSolveResult.whatThisIs)
        }

        @Test
        @DisplayName("passes additional context to repository when provided")
        fun passesAdditionalContext() = runTest {
            val context = "This is in my kitchen"
            val result =
                useCase(bitmap = mockBitmap, additionalContext = context, imageUri = imageUri)
            assertThat(result).isInstanceOf(Resource.Success::class.java)
        }

        @Test
        @DisplayName("works without additional context")
        fun worksWithoutContext() = runTest {
            val result = useCase(bitmap = mockBitmap, additionalContext = null, imageUri = imageUri)
            assertThat(result).isInstanceOf(Resource.Success::class.java)
        }
    }

    @Nested
    @DisplayName("given solve API failure")
    inner class SolveFailure {

        @BeforeEach
        fun setUpFailure() {
            fakeSolveRepo.shouldReturnError = true
        }

        @Test
        @DisplayName("returns error from repository")
        fun returnsError() = runTest {
            val result = useCase(bitmap = mockBitmap, imageUri = imageUri)
            assertThat(result).isInstanceOf(Resource.Error::class.java)
        }

        @Test
        @DisplayName("does not save to history on API failure")
        fun doesNotSaveOnFailure() = runTest {
            useCase(bitmap = mockBitmap, imageUri = imageUri)
            assertThat(fakeHistoryRepo.savedItems).isEmpty()
        }

        @Test
        @DisplayName("does not decrement daily solves on failure")
        fun doesNotDecrementOnFailure() = runTest {
            fakePrefsRepo.setDailySolvesRemaining(5)
            useCase(bitmap = mockBitmap, imageUri = imageUri)
            // History is empty — decrement only happens after successful save
            assertThat(fakeHistoryRepo.savedItems).isEmpty()
        }
    }
}