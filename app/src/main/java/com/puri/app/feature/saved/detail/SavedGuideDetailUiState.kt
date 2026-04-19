package com.puri.app.feature.saved.detail

import com.puri.app.domain.model.GuideContent
import com.puri.app.domain.model.SavedGuide

sealed interface SavedGuideDetailUiState {
    data object Loading : SavedGuideDetailUiState
    data object Error : SavedGuideDetailUiState
    data class Content(
        val guide: SavedGuide,
        val content: GuideContent?
    ) : SavedGuideDetailUiState
}