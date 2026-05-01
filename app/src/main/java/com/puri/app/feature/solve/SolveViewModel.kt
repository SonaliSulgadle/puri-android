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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import javax.inject.Inject

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

    // Non-idle state — null = "show the idle/home screen"
    private val _activeState = MutableStateFlow<SolveUiState?>(null)

    private val idleData: StateFlow<SolveUiState.Idle> = combine(
        getDailySolvesRemainingUseCase(),
        getHistoryUseCase()
    ) { remaining, history ->
        SolveUiState.Idle(
            dailySolvesRemaining = remaining,
            recentSolves = history.take(4)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SolveUiState.Idle()
    )

    val uiState: StateFlow<SolveUiState> = combine(
        _activeState,
        idleData
    ) { active, idle ->
        active ?: idle
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SolveUiState.Idle()
    )

    fun onIntent(intent: SolveIntent) {
        when (intent) {
            SolveIntent.OpenCamera ->
                _activeState.value = SolveUiState.CameraOpen

            SolveIntent.OpenGallery -> Unit
            is SolveIntent.ImageCaptured ->
                handleImageCaptured(intent)

            is SolveIntent.GalleryImageSelected ->
                handleImageCaptured(SolveIntent.ImageCaptured(intent.bitmap, intent.imageUri))

            is SolveIntent.TextQueryChanged ->
                currentTextQuery = intent.query

            SolveIntent.SubmitTextQuery ->
                handleTextQuery()

            SolveIntent.Retry,
            SolveIntent.ClearResult ->
                returnToIdle()

            SolveIntent.SaveResult ->
                handleSaveResult()

            is SolveIntent.AdditionalContextChanged ->
                additionalContext = intent.context
        }
    }

    private fun sendEffect(effect: SolveUiEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private fun returnToIdle() {
        additionalContext = ""
        currentTextQuery = ""
        _activeState.value = null  // null → combine shows idleData
    }

    private fun handleImageCaptured(intent: SolveIntent.ImageCaptured) {
        analytics.log(PuriEvent.SolveStarted("image"))
        viewModelScope.launch {
            _activeState.value = SolveUiState.Loading

            // Step 2: Haptic feedback
            sendEffect(SolveUiEffect.TriggerHaptic)
            yield()

            val compressed = withContext(Dispatchers.Default) {
                intent.bitmap.compressForGemini()
            }

            val startTime = System.currentTimeMillis()
            val result = solveImageUseCase(
                bitmap = compressed,
                imageUri = intent.imageUri,
                additionalContext = additionalContext.ifBlank { null }
            )

            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < 1500) delay(1500 - elapsed)

            when (result) {
                is Resource.Success -> {
                    val duration = System.currentTimeMillis() - startTime
                    analytics.log(
                        PuriEvent.SolveCompleted(
                            type = "image",
                            category = result.data.category.name.lowercase(),
                            confidence = result.data.confidenceLevel.name.lowercase(),
                            durationMs = duration
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
    }

    private fun handleTextQuery() {
        analytics.log(PuriEvent.SolveStarted("text"))
        val startTime = System.currentTimeMillis()
        if (currentTextQuery.isBlank()) {
            sendEffect(SolveUiEffect.ShowSnackbar(R.string.error_empty_query))
            return
        }
        viewModelScope.launch {
            _activeState.value = SolveUiState.Loading
            yield()

            val result = solveTextUseCase(currentTextQuery)

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

    private fun handleSaveResult() {
        val state = _activeState.value as? SolveUiState.Success ?: return
        viewModelScope.launch {
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
                    sendEffect(SolveUiEffect.ShowSaveConfirmation)
                }

                is Resource.Error ->
                    sendEffect(SolveUiEffect.ShowSnackbar(R.string.error_unknown))

                Resource.Loading -> Unit
            }
        }
    }
}