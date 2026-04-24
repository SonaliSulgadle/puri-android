package com.puri.app.feature.history.detail

import com.puri.app.domain.model.HistoryItem

sealed interface HistoryDetailUiState {
    data object Loading : HistoryDetailUiState
    data object Error : HistoryDetailUiState
    data class Content(
        val historyItem: HistoryItem,
        val isSaved: Boolean
    ) : HistoryDetailUiState
}