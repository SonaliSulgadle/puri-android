package com.puri.app.feature.saved.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.core.analytics.Analytics
import com.puri.app.core.analytics.FeatureNames
import com.puri.app.core.analytics.PuriEvent
import com.puri.app.data.guides.GuideContentLoader
import com.puri.app.domain.usecase.GetSavedGuidesUseCase
import com.puri.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SavedGuideDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSavedGuidesUseCase: GetSavedGuidesUseCase,
    private val guideContentLoader: GuideContentLoader,
    private val analytics: Analytics
) : ViewModel() {

    private val guideId: Long = checkNotNull(
        savedStateHandle[Screen.SavedDetail.ARG]
    )

    val uiState: StateFlow<SavedGuideDetailUiState> = getSavedGuidesUseCase()
        .map { guides ->

            val guide = guides.find { it.id == guideId }
                ?: return@map SavedGuideDetailUiState.Error

            val content = guide.guideKey?.let { key ->
                withContext(Dispatchers.IO) {
                    guideContentLoader.loadContent(key)
                }
            }

            SavedGuideDetailUiState.Content(guide = guide, content = content)
        }
        .catch { e ->
            emit(SavedGuideDetailUiState.Error)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SavedGuideDetailUiState.Loading
        )

    init {
        analytics.log(
            PuriEvent.GuideOpened(
                guideKey = guideId.toString(),
                isPreBundled = true
            )
        )
        analytics.log(PuriEvent.FeatureDiscovered(FeatureNames.GUIDE_DETAIL))
    }
}
