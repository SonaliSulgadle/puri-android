package com.puri.app.feature.history.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.core.common.Resource
import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.usecase.GetHistoryUseCase
import com.puri.app.domain.usecase.SaveGuideUseCase
import com.puri.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getHistoryUseCase: GetHistoryUseCase,
    private val saveGuideUseCase: SaveGuideUseCase
) : ViewModel() {

    private val historyItemId: Long =
        checkNotNull(savedStateHandle[Screen.HistoryDetail.ARG])

    private val _savedOverride = MutableStateFlow<Boolean?>(null)

    val uiState: StateFlow<HistoryDetailUiState> = combine(
        getHistoryUseCase(),
        _savedOverride
    ) { items, savedOverride ->
        val item = items.find { it.id == historyItemId }
            ?: return@combine HistoryDetailUiState.Error

        HistoryDetailUiState.Content(
            historyItem = item,
            isSaved = savedOverride ?: item.isSaved
        )
    }
        .catch { emit(HistoryDetailUiState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryDetailUiState.Loading
        )

    fun saveResult() {
        val state = uiState.value as? HistoryDetailUiState.Content ?: return
        if (state.isSaved) return

        viewModelScope.launch {
            val guide = SavedGuide(
                title = state.historyItem.solveResult.whatThisIs,
                description = state.historyItem.solveResult.description,
                guideKey = null,
                category = state.historyItem.solveResult.category,
                solveResult = state.historyItem.solveResult,
                isPreBundled = false,
                isFeatured = false,
                imageUri = state.historyItem.solveResult.imageUri
            )
            if (saveGuideUseCase(guide) is Resource.Success) {
                _savedOverride.value = true
            }
        }
    }
}