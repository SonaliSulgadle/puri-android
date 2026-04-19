package com.puri.app.feature.saved.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.data.guides.GuideContentLoader
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

    val uiState: StateFlow<SavedGuideDetailUiState> = getSavedGuidesUseCase()
        .map { guides ->
            Log.d("SavedDetail", "Looking for guideId=$guideId in ${guides.size} guides")
            guides.forEach {
                Log.d(
                    "SavedDetail",
                    "  id=${it.id} guideKey=${it.guideKey} isPreBundled=${it.isPreBundled}"
                )
            }

            val guide = guides.find { it.id == guideId }
                ?: return@map SavedGuideDetailUiState.Error

            Log.d("SavedDetail", "Found guide: guideKey=${guide.guideKey}")

            val content = guide.guideKey?.let { key ->
                guideContentLoader.loadContent(key).also {
                    Log.d("SavedDetail", "Content loaded: ${it?.sections?.size} sections")
                }
            }

            SavedGuideDetailUiState.Content(guide = guide, content = content)
        }
        .catch { e ->
            Log.e("SavedDetail", "Error", e)
            emit(SavedGuideDetailUiState.Error)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SavedGuideDetailUiState.Loading
        )
}
