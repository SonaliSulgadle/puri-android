package com.puri.app.feature.solve

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.R
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SolveViewModel @Inject constructor(
    private val solveImageUseCase: SolveImageUseCase,
    private val solveTextUseCase: SolveTextUseCase,
    private val saveGuideUseCase: SaveGuideUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val getDailySolvesRemainingUseCase: GetDailySolvesRemainingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SolveUiState>(SolveUiState.Idle())
    val uiState: StateFlow<SolveUiState> = _uiState.asStateFlow()

    private val _effects = Channel<SolveUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var currentTextQuery = ""
    private var additionalContext = ""

    private val idleData = combine(
        getDailySolvesRemainingUseCase(),
        getHistoryUseCase()
    ) { remaining, history ->
        SolveUiState.Idle(
            dailySolvesRemaining = remaining,
            recentSolves = history.take(4)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SolveUiState.Idle()
    )

    init {
        viewModelScope.launch {
            idleData.collect { idle ->
                if (_uiState.value is SolveUiState.Idle) {
                    _uiState.value = idle
                }
            }
        }
    }

    fun onIntent(intent: SolveIntent) {
        when (intent) {
            SolveIntent.OpenCamera -> _uiState.value = SolveUiState.CameraOpen
            SolveIntent.OpenGallery -> Unit
            is SolveIntent.ImageCaptured -> handleImageCaptured(intent)
            is SolveIntent.GalleryImageSelected -> handleImageCaptured(
                SolveIntent.ImageCaptured(intent.bitmap, intent.imageUri)
            )

            is SolveIntent.TextQueryChanged -> {
                currentTextQuery = intent.query
                if (_uiState.value is SolveUiState.Idle) {
                    _uiState.update {
                        (it as? SolveUiState.Idle)?.copy(currentQuery = intent.query) ?: it
                    }
                }
            }

            SolveIntent.SubmitTextQuery -> handleTextQuery()
            SolveIntent.Retry, SolveIntent.ClearResult -> returnToIdle()
            SolveIntent.SaveResult -> handleSaveResult()
            is SolveIntent.AdditionalContextChanged -> additionalContext = intent.context
        }
    }

    private fun sendEffect(effect: SolveUiEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private fun returnToIdle() {
        additionalContext = ""
        currentTextQuery = ""
        _uiState.value = idleData.value
    }

    private fun handleImageCaptured(intent: SolveIntent.ImageCaptured) {
        viewModelScope.launch {
            sendEffect(SolveUiEffect.TriggerHaptic)
            val compressed = intent.bitmap.compressForGemini()
            _uiState.value = SolveUiState.Loading

            val result = solveImageUseCase(
                bitmap = compressed,
                imageUri = intent.imageUri,
                additionalContext = additionalContext.ifBlank { null }
            )

            when (result) {
                is Resource.Success -> handleSolveSuccess(result.data, compressed)
                is Resource.Error -> handleSolveError(result.error)
                Resource.Loading -> Unit
            }
        }
    }

    private fun handleTextQuery() {
        if (currentTextQuery.isBlank()) {
            sendEffect(SolveUiEffect.ShowSnackbar(R.string.error_empty_query))
            return
        }
        viewModelScope.launch {
            _uiState.value = SolveUiState.Loading
            when (val result = solveTextUseCase(currentTextQuery)) {
                is Resource.Success -> handleSolveSuccess(result.data, null)
                is Resource.Error -> handleSolveError(result.error)
                Resource.Loading -> Unit
            }
        }
    }

    private fun handleSolveSuccess(
        result: SolveResult,
        bitmap: Bitmap?
    ) {
        _uiState.value = when (result.confidenceLevel) {
            ConfidenceLevel.HIGH -> SolveUiState.Success(result)
            ConfidenceLevel.LOW -> SolveUiState.Uncertain(bitmap)
            ConfidenceLevel.UNSAFE -> SolveUiState.UnsafeContent
        }
    }

    private fun handleSolveError(error: PuriError) {
        _uiState.value = when (error) {
            PuriError.DailyLimitReached -> SolveUiState.DailyLimitReached
            else -> {
                sendEffect(SolveUiEffect.ShowSnackbar(error.toMessageRes()))
                idleData.value
            }
        }
    }

    private fun handleSaveResult() {
        val state = _uiState.value as? SolveUiState.Success ?: return
        viewModelScope.launch {
            val guide = SavedGuide(
                title = state.result.whatThisIs,
                description = state.result.description,
                category = state.result.category,
                solveResult = state.result,
                isPreBundled = false,
                isFeatured = false,
                imageUri = state.result.imageUri
            )
            when (saveGuideUseCase(guide)) {
                is Resource.Success -> {
                    _uiState.update {
                        (it as? SolveUiState.Success)?.copy(isSaved = true) ?: it
                    }
                    sendEffect(SolveUiEffect.ShowSaveConfirmation)
                }

                is Resource.Error -> sendEffect(
                    SolveUiEffect.ShowSnackbar(R.string.error_unknown)
                )

                Resource.Loading -> Unit
            }
        }
    }
}