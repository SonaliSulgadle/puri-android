package com.puri.app.feature.solve

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.R
import com.puri.app.core.analytics.Analytics
import com.puri.app.core.analytics.PuriEvent
import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.core.common.compressForGemini
import com.puri.app.core.ui.mapper.toMessageRes
import com.puri.app.domain.model.ConfidenceLevel
import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.model.SolveResult
import com.puri.app.domain.usecase.GetDailySolvesRemainingUseCase
import com.puri.app.domain.usecase.GetHistoryUseCase
import com.puri.app.domain.usecase.SaveGuideUseCase
import com.puri.app.domain.usecase.SolveImageUseCase
import com.puri.app.domain.usecase.SolveTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SolveViewModel @Inject constructor(
    private val solveImageUseCase: SolveImageUseCase,
    private val solveTextUseCase: SolveTextUseCase,
    private val saveGuideUseCase: SaveGuideUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val getDailySolvesRemainingUseCase: GetDailySolvesRemainingUseCase,
    private val analytics: Analytics
) : ViewModel() {

    private val _effects = Channel<SolveUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var currentTextQuery = ""
    private var additionalContext = ""
    private var activeJob: Job? = null

    private val _activeState = MutableStateFlow<SolveUiState?>(null)

    private val idleData: StateFlow<SolveUiState.Idle> = combine(
        getDailySolvesRemainingUseCase(),
        getHistoryUseCase()
    ) { remaining, history ->
        SolveUiState.Idle(
            dailySolvesRemaining = remaining,
            recentSolves = history.take(4)
        )
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SolveUiState.Idle()
        )

    val uiState: StateFlow<SolveUiState> = _activeState
        .flatMapLatest { active ->
            if (active != null) flowOf(active) else idleData
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SolveUiState.Idle()
        )

    fun onIntent(intent: SolveIntent) {
        when (intent) {

            SolveIntent.OpenCamera ->
                _activeState.value = SolveUiState.CameraOpen

            SolveIntent.OpenGallery -> Unit

            is SolveIntent.ImageCaptured -> {
                _activeState.value = SolveUiState.Loading
                activeJob?.cancel()
                activeJob = viewModelScope.launch {
                    doImageSolve(intent)
                }
            }

            is SolveIntent.GalleryImageSelected -> {
                _activeState.value = SolveUiState.Loading
                activeJob?.cancel()
                activeJob = viewModelScope.launch {
                    doImageSolve(
                        SolveIntent.ImageCaptured(intent.bitmap, intent.imageUri)
                    )
                }
            }

            is SolveIntent.TextQueryChanged ->
                currentTextQuery = intent.query

            SolveIntent.SubmitTextQuery -> {
                if (currentTextQuery.isBlank()) {
                    viewModelScope.launch {
                        _effects.send(SolveUiEffect.ShowSnackbar(R.string.error_empty_query))
                    }
                    return
                }
                _activeState.value = SolveUiState.Loading
                activeJob?.cancel()
                activeJob = viewModelScope.launch {
                    doTextSolve()
                }
            }

            SolveIntent.Retry,
            SolveIntent.ClearResult -> returnToIdle()

            SolveIntent.SaveResult ->
                viewModelScope.launch { doSaveResult() }

            is SolveIntent.AdditionalContextChanged ->
                additionalContext = intent.context
        }
    }

    private fun sendEffect(effect: SolveUiEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private fun returnToIdle() {
        activeJob?.cancel()
        activeJob = null
        additionalContext = ""
        currentTextQuery = ""
        _activeState.value = null
    }


    private suspend fun doImageSolve(intent: SolveIntent.ImageCaptured) {
        _effects.send(SolveUiEffect.TriggerHaptic)
        analytics.log(PuriEvent.SolveStarted("image"))

        val startTime = System.currentTimeMillis()

        val compressed = withContext(Dispatchers.Default) {
            intent.bitmap.compressForGemini().also { result ->
                if (result !== intent.bitmap && !intent.bitmap.isRecycled) {
                    intent.bitmap.recycle()
                }
            }
        }

        val result = solveImageUseCase(
            bitmap = compressed,
            imageUri = intent.imageUri,
            additionalContext = additionalContext.ifBlank { null }
        )

        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed < 1500L) delay(1500L - elapsed)

        when (result) {
            is Resource.Success -> {
                analytics.log(
                    PuriEvent.SolveCompleted(
                        type = "image",
                        category = result.data.category.name.lowercase(),
                        confidence = result.data.confidenceLevel.name.lowercase(),
                        durationMs = System.currentTimeMillis() - startTime
                    )
                )
                handleSolveSuccess(result.data)
            }

            is Resource.Error -> {
                analytics.log(PuriEvent.SolveFailed("image", result.error.javaClass.simpleName))
                handleSolveError(result.error)
            }

            Resource.Loading -> Unit
        }
    }

    private suspend fun doTextSolve() {
        analytics.log(PuriEvent.SolveStarted("text"))
        val startTime = System.currentTimeMillis()

        val result = solveTextUseCase(currentTextQuery)

        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed < 800L) delay(800L - elapsed)

        when (result) {
            is Resource.Success -> {
                analytics.log(
                    PuriEvent.SolveCompleted(
                        type = "text",
                        category = result.data.category.name.lowercase(),
                        confidence = result.data.confidenceLevel.name.lowercase(),
                        durationMs = System.currentTimeMillis() - startTime
                    )
                )
                handleSolveSuccess(result.data)
            }

            is Resource.Error -> {
                analytics.log(PuriEvent.SolveFailed("text", result.error.javaClass.simpleName))
                handleSolveError(result.error)
            }

            Resource.Loading -> Unit
        }
    }

    private fun handleSolveSuccess(result: SolveResult) {
        _activeState.value = when (result.confidenceLevel) {
            ConfidenceLevel.HIGH -> SolveUiState.Success(result)
            ConfidenceLevel.LOW -> SolveUiState.Uncertain(null)
            ConfidenceLevel.UNSAFE -> SolveUiState.UnsafeContent
        }
    }

    private fun handleSolveError(error: PuriError) {
        when (error) {
            PuriError.DailyLimitReached ->
                _activeState.value = SolveUiState.DailyLimitReached

            else -> {
                sendEffect(SolveUiEffect.ShowSnackbar(error.toMessageRes()))
                returnToIdle()
            }
        }
    }

    private suspend fun doSaveResult() {
        val state = _activeState.value as? SolveUiState.Success ?: return
        val guide = SavedGuide(
            title = state.result.whatThisIs,
            description = state.result.description,
            guideKey = null,
            category = state.result.category,
            solveResult = state.result,
            isPreBundled = false,
            isFeatured = false,
            imageUri = state.result.imageUri
        )
        when (saveGuideUseCase(guide)) {
            is Resource.Success -> {
                _activeState.value = state.copy(isSaved = true)
                _effects.send(SolveUiEffect.ShowSaveConfirmation)
                analytics.log(PuriEvent.ResultSaved(state.result.category.name.lowercase()))
            }

            is Resource.Error ->
                _effects.send(SolveUiEffect.ShowSnackbar(R.string.error_unknown))

            Resource.Loading -> Unit
        }
    }
}