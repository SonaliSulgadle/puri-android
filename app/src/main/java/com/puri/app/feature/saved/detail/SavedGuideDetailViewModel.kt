package com.puri.app.feature.saved.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.data.guides.GuideContentLoader
import com.puri.app.domain.model.GuideContent
import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.usecase.GetSavedGuidesUseCase
import com.puri.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SavedGuideDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSavedGuidesUseCase: GetSavedGuidesUseCase,
    private val guideContentLoader: GuideContentLoader
) : ViewModel() {

    private val guideId: Long = checkNotNull(
        savedStateHandle[Screen.SavedDetail.ARG]
    )

    val uiState: StateFlow<SavedGuideDetailUiState> =
        getSavedGuidesUseCase()
            .map { guides ->
                val guide = guides.find { it.id == guideId }
                    ?: return@map SavedGuideDetailUiState.Error

                val content = guide.guideKey
                    ?.let { guideContentLoader.loadContent(it) }

                SavedGuideDetailUiState.Content(
                    guide = guide,
                    content = content
                )
            }
            .catch { emit(SavedGuideDetailUiState.Error) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SavedGuideDetailUiState.Loading
            )
}

sealed interface SavedGuideDetailUiState {
    data object Loading : SavedGuideDetailUiState
    data object Error : SavedGuideDetailUiState
    data class Content(
        val guide: SavedGuide,
        val content: GuideContent?
    ) : SavedGuideDetailUiState
}