package com.puri.app.feature.saved

import com.puri.app.domain.model.SavedGuide

sealed interface SavedIntent {
    data class DeleteGuide(val id: Long) : SavedIntent
    data class OpenGuide(val guide: SavedGuide) : SavedIntent
    data object NavigateToSolve : SavedIntent
}

sealed interface SavedUiState {
    data object Loading : SavedUiState
    data class Content(
        val preBundledGuides: List<SavedGuide>,
        val userSavedGuides: List<SavedGuide>,
        val featuredGuide: SavedGuide?,
        val isOfflineMode: Boolean
    ) : SavedUiState

    data object Empty : SavedUiState
}

sealed interface SavedUiEffect {
    data class OpenGuideDetail(val guideId: Long) : SavedUiEffect
    data object NavigateToSolve : SavedUiEffect
}