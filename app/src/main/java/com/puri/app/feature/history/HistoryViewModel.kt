package com.puri.app.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.R
import com.puri.app.core.util.DateTimeUtils
import com.puri.app.domain.model.HistoryItem
import com.puri.app.domain.usecase.DeleteHistoryItemUseCase
import com.puri.app.domain.usecase.GetHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val deleteHistoryItemUseCase: DeleteHistoryItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _effects = Channel<HistoryUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var allItems: List<HistoryItem> = emptyList()

    init {
        observeHistory()
    }

    fun onIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.DeleteItem -> deleteItem(intent.id)
            is HistoryIntent.SearchQueryChanged -> filterItems(intent.query)
            HistoryIntent.ClearSearch -> filterItems("")
            is HistoryIntent.ViewItem -> viewItem(intent.id)
            is HistoryIntent.ToggleSaved -> toggleSaved(intent.id)
            HistoryIntent.LoadHistory -> observeHistory()
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            getHistoryUseCase()
                .catch {
                    _uiState.value = HistoryUiState.Error(R.string.error_unknown)
                }
                .collect { items ->
                    allItems = items
                    if (items.isEmpty()) {
                        _uiState.value = HistoryUiState.Empty
                    } else {
                        _uiState.value = HistoryUiState.Content(
                            groupedItems = groupByDate(items)
                        )
                    }
                }
        }
    }

    private fun groupByDate(items: List<HistoryItem>): Map<String, List<HistoryItem>> =
        DateTimeUtils.groupByDateLabel(items) { it.timestamp }

    private fun filterItems(query: String) {
        val current = _uiState.value as? HistoryUiState.Content ?: return
        val filtered = if (query.isBlank()) {
            current.groupedItems
        } else {
            allItems
                .filter { item ->
                    item.solveResult.whatThisIs.contains(query, ignoreCase = true) ||
                            item.solveResult.category.name.contains(query, ignoreCase = true)
                }
                .let { groupByDate(it) }
        }
        _uiState.value = current.copy(
            searchQuery = query,
            filteredItems = filtered
        )
    }

    private fun deleteItem(id: Long) {
        viewModelScope.launch {
            deleteHistoryItemUseCase(id)
        }
    }

    private fun toggleSaved(id: Long) {
        // V2 — connect to SavedGuidesRepository.toggleSaved
    }

    private fun viewItem(id: Long) {
        val item = allItems.find { it.id == id } ?: return
        viewModelScope.launch {
            _effects.send(HistoryUiEffect.NavigateToResult(item))
        }
    }
}