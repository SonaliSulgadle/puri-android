package com.puri.app.feature.history

import com.puri.app.domain.model.HistoryItem

sealed interface HistoryIntent {
    data object LoadHistory : HistoryIntent
    data class DeleteItem(val id: Long) : HistoryIntent
    data class ToggleSaved(val id: Long) : HistoryIntent
    data class SearchQueryChanged(val query: String) : HistoryIntent
    data object ClearSearch : HistoryIntent
    data class ViewItem(val id: Long) : HistoryIntent
}

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Content(
        val groupedItems: Map<String, List<HistoryItem>>,
        val searchQuery: String = "",
        val filteredItems: Map<String, List<HistoryItem>> = groupedItems
    ) : HistoryUiState

    data object Empty : HistoryUiState
    data class Error(val messageRes: Int) : HistoryUiState
}

sealed interface HistoryUiEffect {
    data class ShowSnackbar(val messageRes: Int) : HistoryUiEffect
    data class NavigateToResult(val item: HistoryItem) : HistoryUiEffect
}