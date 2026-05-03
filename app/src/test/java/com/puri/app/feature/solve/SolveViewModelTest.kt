package com.puri.app.feature.solve

import android.graphics.Bitmap
import app.cash.turbine.test
import com.puri.app.core.analytics.Analytics
import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.domain.model.ConfidenceLevel
import com.puri.app.domain.usecase.GetDailySolvesRemainingUseCase
import com.puri.app.domain.usecase.GetHistoryUseCase
import com.puri.app.domain.usecase.SaveGuideUseCase
import com.puri.app.domain.usecase.SolveImageUseCase
import com.puri.app.domain.usecase.SolveTextUseCase
import com.puri.app.util.TestFixtures
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("SolveViewModel")
class SolveViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val solveImageUseCase: SolveImageUseCase = mockk()
    private val solveTextUseCase: SolveTextUseCase = mockk()
    private val saveGuideUseCase: SaveGuideUseCase = mockk()
    private val getHistoryUseCase: GetHistoryUseCase = mockk()
    private val getDailySolvesRemainingUseCase: GetDailySolvesRemainingUseCase = mockk()

    private val mockBitmap: Bitmap = mockk(relaxed = true)

    private val analytics = mockk<Analytics>(relaxed = true)
    private lateinit var viewModel: SolveViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { solveImageUseCase(any(), any(), any()) } returns
                Resource.Success(TestFixtures.trashSolveResult)

        coEvery { solveTextUseCase(any()) } returns
                Resource.Success(TestFixtures.trashSolveResult)

        coEvery { saveGuideUseCase(any()) } returns Resource.Success(Unit)

        coEvery { getHistoryUseCase() } returns flowOf(emptyList())

        coEvery { getDailySolvesRemainingUseCase() } returns flowOf(10)

        viewModel = SolveViewModel(
            solveImageUseCase = solveImageUseCase,
            solveTextUseCase = solveTextUseCase,
            saveGuideUseCase = saveGuideUseCase,
            getHistoryUseCase = getHistoryUseCase,
            getDailySolvesRemainingUseCase = getDailySolvesRemainingUseCase,
            analytics = analytics
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.navigateToSuccess() {
        viewModel.onIntent(SolveIntent.ImageCaptured(mockBitmap, null))
        advanceUntilIdle()
        assertThat(viewModel.uiState.value).isInstanceOf(SolveUiState.Success::class.java)
    }

    @Nested
    @DisplayName("initial state")
    inner class InitialState {

        @Test
        @DisplayName("starts in Idle state")
        fun startsInIdle() = runTest {
            advanceUntilIdle()
            assertThat(viewModel.uiState.value).isInstanceOf(SolveUiState.Idle::class.java)
        }

        @Test
        @DisplayName("idle state shows correct daily solves remaining")
        fun idleShowsCorrectRemaining() = runTest {
            coEvery { getDailySolvesRemainingUseCase() } returns flowOf(7)
            viewModel = SolveViewModel(
                solveImageUseCase, solveTextUseCase, saveGuideUseCase,
                getHistoryUseCase, getDailySolvesRemainingUseCase, analytics
            )
            advanceUntilIdle()
            val state = viewModel.uiState.value as SolveUiState.Idle
            assertThat(state.dailySolvesRemaining).isEqualTo(7)
        }

        @Test
        @DisplayName("idle state shows recent history items")
        fun idleShowsHistory() = runTest {
            coEvery { getHistoryUseCase() } returns
                    flowOf(listOf(TestFixtures.historyItemToday, TestFixtures.historyItemYesterday))
            viewModel = SolveViewModel(
                solveImageUseCase, solveTextUseCase, saveGuideUseCase,
                getHistoryUseCase, getDailySolvesRemainingUseCase, analytics
            )
            advanceUntilIdle()
            val state = viewModel.uiState.value as SolveUiState.Idle
            assertThat(state.recentSolves).hasSize(2)
        }
    }

    @Nested
    @DisplayName("camera intent")
    inner class CameraIntent {

        @Test
        @DisplayName("OpenCamera sets CameraOpen state")
        fun openCameraSetsCameraOpenState() = runTest {
            viewModel.onIntent(SolveIntent.OpenCamera)
            assertThat(viewModel.uiState.value).isEqualTo(SolveUiState.CameraOpen)
        }
    }

    @Nested
    @DisplayName("image capture — state transitions")
    inner class ImageCaptureStateTransitions {

        @Test
        @DisplayName("sets Loading state immediately after capture")
        fun setsLoadingStateImmediately() = runTest {
            viewModel.uiState.test {
                awaitItem() // initial Idle

                viewModel.onIntent(SolveIntent.ImageCaptured(mockBitmap, null))

                val loadingState = awaitItem()
                assertThat(loadingState).isEqualTo(SolveUiState.Loading)

                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("transitions to Success when use case returns HIGH confidence result")
        fun transitionsToSuccessOnHighConfidence() = runTest {
            coEvery { solveImageUseCase(any(), any(), any()) } returns
                    Resource.Success(TestFixtures.trashSolveResult) // HIGH confidence

            viewModel.onIntent(SolveIntent.ImageCaptured(mockBitmap, null))
            advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(SolveUiState.Success::class.java)
        }

        @Test
        @DisplayName("Success state contains the correct solve result")
        fun successStateContainsCorrectResult() = runTest {
            coEvery { solveImageUseCase(any(), any(), any()) } returns
                    Resource.Success(TestFixtures.trashSolveResult)

            viewModel.onIntent(SolveIntent.ImageCaptured(mockBitmap, null))
            advanceUntilIdle()

            val state = viewModel.uiState.value as SolveUiState.Success
            assertThat(state.result.whatThisIs).isEqualTo("LDPE Plastic Container")
        }

        @Test
        @DisplayName("transitions to Uncertain when use case returns LOW confidence result")
        fun transitionsToUncertainOnLowConfidence() = runTest {
            coEvery { solveImageUseCase(any(), any(), any()) } returns
                    Resource.Success(TestFixtures.uncertainSolveResult) // LOW confidence

            viewModel.onIntent(SolveIntent.ImageCaptured(mockBitmap, null))
            advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(SolveUiState.Uncertain::class.java)
        }

        @Test
        @DisplayName("transitions to UnsafeContent when use case returns UNSAFE confidence")
        fun transitionsToUnsafeOnUnsafeConfidence() = runTest {
            coEvery { solveImageUseCase(any(), any(), any()) } returns
                    Resource.Success(
                        TestFixtures.trashSolveResult.copy(confidenceLevel = ConfidenceLevel.UNSAFE)
                    )

            viewModel.onIntent(SolveIntent.ImageCaptured(mockBitmap, null))
            advanceUntilIdle()

            assertThat(viewModel.uiState.value).isEqualTo(SolveUiState.UnsafeContent)
        }

        @Test
        @DisplayName("transitions to DailyLimitReached when use case returns DailyLimitReached error")
        fun transitionsToDailyLimitOnError() = runTest {
            coEvery { solveImageUseCase(any(), any(), any()) } returns
                    Resource.Error(PuriError.DailyLimitReached)

            viewModel.onIntent(SolveIntent.ImageCaptured(mockBitmap, null))
            advanceUntilIdle()

            assertThat(viewModel.uiState.value).isEqualTo(SolveUiState.DailyLimitReached)
        }

        @Test
        @DisplayName("returns to Idle and emits snackbar on unknown error")
        fun returnsToIdleOnUnknownError() = runTest {
            coEvery { solveImageUseCase(any(), any(), any()) } returns
                    Resource.Error(PuriError.Unknown())

            viewModel.effects.test {
                viewModel.onIntent(SolveIntent.ImageCaptured(mockBitmap, null))
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(SolveUiEffect.TriggerHaptic)
                assertThat(awaitItem()).isInstanceOf(SolveUiEffect.ShowSnackbar::class.java)

                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("emits TriggerHaptic effect on capture")
        fun emitsHapticEffectOnCapture() = runTest {
            viewModel.effects.test {
                viewModel.onIntent(SolveIntent.ImageCaptured(mockBitmap, null))

                assertThat(awaitItem()).isEqualTo(SolveUiEffect.TriggerHaptic)

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    @DisplayName("text query flow")
    inner class TextQueryFlow {

        @Test
        @DisplayName("blank query emits ShowSnackbar without calling use case")
        fun blankQueryEmitsSnackbar() = runTest {
            viewModel.onIntent(SolveIntent.TextQueryChanged(""))

            viewModel.effects.test {
                viewModel.onIntent(SolveIntent.SubmitTextQuery)
                advanceUntilIdle()

                assertThat(awaitItem()).isInstanceOf(SolveUiEffect.ShowSnackbar::class.java)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("whitespace query emits ShowSnackbar")
        fun whitespaceQueryEmitsSnackbar() = runTest {
            viewModel.onIntent(SolveIntent.TextQueryChanged("   "))

            viewModel.effects.test {
                viewModel.onIntent(SolveIntent.SubmitTextQuery)
                advanceUntilIdle()

                assertThat(awaitItem()).isInstanceOf(SolveUiEffect.ShowSnackbar::class.java)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("valid text query transitions to Success")
        fun validQueryTransitionsToSuccess() = runTest {
            coEvery { solveTextUseCase(any()) } returns
                    Resource.Success(TestFixtures.textSolveResult)

            viewModel.onIntent(SolveIntent.TextQueryChanged("How do I sort trash?"))
            viewModel.onIntent(SolveIntent.SubmitTextQuery)
            advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(SolveUiState.Success::class.java)
        }

        @Test
        @DisplayName("text query success state contains correct result")
        fun textQuerySuccessContainsResult() = runTest {
            coEvery { solveTextUseCase(any()) } returns
                    Resource.Success(TestFixtures.textSolveResult)

            viewModel.onIntent(SolveIntent.TextQueryChanged("Subway top-up"))
            viewModel.onIntent(SolveIntent.SubmitTextQuery)
            advanceUntilIdle()

            val state = viewModel.uiState.value as SolveUiState.Success
            assertThat(state.result.whatThisIs).isEqualTo("Subway T-Money Card Top-up")
        }
    }

    @Nested
    @DisplayName("save result")
    inner class SaveResult {

        @Test
        @DisplayName("isSaved becomes true after saving")
        fun isSavedBecomesTrueAfterSave() = runTest {
            navigateToSuccess()

            viewModel.onIntent(SolveIntent.SaveResult)
            advanceUntilIdle()

            assertThat((viewModel.uiState.value as SolveUiState.Success).isSaved).isTrue()
        }

        @Test
        @DisplayName("emits ShowSaveConfirmation after saving")
        fun emitsSaveConfirmation() = runTest {
            navigateToSuccess()

            viewModel.effects.test {
                assertThat(awaitItem()).isEqualTo(SolveUiEffect.TriggerHaptic)

                viewModel.onIntent(SolveIntent.SaveResult)
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(SolveUiEffect.ShowSaveConfirmation)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("save does nothing when not in Success state")
        fun saveIsNoOpWhenNotInSuccess() = runTest {
            advanceUntilIdle()
            assertThat(viewModel.uiState.value).isInstanceOf(SolveUiState.Idle::class.java)

            viewModel.effects.test {
                viewModel.onIntent(SolveIntent.SaveResult)
                advanceUntilIdle()

                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("save emits snackbar when saveGuideUseCase fails")
        fun saveEmitsSnackbarOnFailure() = runTest {
            coEvery { saveGuideUseCase(any()) } returns Resource.Error(PuriError.Unknown())
            navigateToSuccess()

            viewModel.effects.test {
                assertThat(awaitItem()).isEqualTo(SolveUiEffect.TriggerHaptic) // drain

                viewModel.onIntent(SolveIntent.SaveResult)
                advanceUntilIdle()

                assertThat(awaitItem()).isInstanceOf(SolveUiEffect.ShowSnackbar::class.java)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    @DisplayName("navigation and reset")
    inner class NavigationAndReset {

        @Test
        @DisplayName("ClearResult returns to Idle from CameraOpen")
        fun clearResultFromCamera() = runTest {
            viewModel.onIntent(SolveIntent.OpenCamera)
            assertThat(viewModel.uiState.value).isEqualTo(SolveUiState.CameraOpen)

            viewModel.onIntent(SolveIntent.ClearResult)
            advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(SolveUiState.Idle::class.java)
        }

        @Test
        @DisplayName("ClearResult returns to Idle from Success")
        fun clearResultFromSuccess() = runTest {
            navigateToSuccess()

            viewModel.onIntent(SolveIntent.ClearResult)
            advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(SolveUiState.Idle::class.java)
        }

        @Test
        @DisplayName("Retry returns to Idle from Uncertain")
        fun retryFromUncertain() = runTest {
            coEvery { solveImageUseCase(any(), any(), any()) } returns
                    Resource.Success(TestFixtures.uncertainSolveResult)

            viewModel.onIntent(SolveIntent.ImageCaptured(mockBitmap, null))
            advanceUntilIdle()
            assertThat(viewModel.uiState.value).isInstanceOf(SolveUiState.Uncertain::class.java)

            viewModel.onIntent(SolveIntent.Retry)
            advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(SolveUiState.Idle::class.java)
        }
    }

    @Nested
    @DisplayName("idle data reactivity")
    inner class IdleDataReactivity {

        @Test
        @DisplayName("idle state shows at most 4 recent solves")
        fun idleShowsAtMostFourSolves() = runTest {
            val sixItems = (1..6).map {
                TestFixtures.historyItemToday.copy(id = it.toLong())
            }
            coEvery { getHistoryUseCase() } returns flowOf(sixItems)
            viewModel = SolveViewModel(
                solveImageUseCase, solveTextUseCase, saveGuideUseCase,
                getHistoryUseCase, getDailySolvesRemainingUseCase, analytics
            )
            advanceUntilIdle()

            val state = viewModel.uiState.value as SolveUiState.Idle
            assertThat(state.recentSolves.size).isLessThanOrEqualTo(4)
        }
    }
}