package com.puri.app.feature.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.domain.model.Category
import com.puri.app.domain.model.PreBundledGuideKey
import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.usecase.GetSavedGuidesUseCase
import com.puri.app.domain.usecase.SeedPreBundledGuidesUseCase
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
class SavedViewModel @Inject constructor(
    private val getSavedGuidesUseCase: GetSavedGuidesUseCase,
    private val seedPreBundledGuidesUseCase: SeedPreBundledGuidesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SavedUiState>(SavedUiState.Loading)
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    private val _effects = Channel<SavedUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            seedPreBundledGuidesUseCase(buildPreBundledGuides())
            loadGuides()
        }
    }

    fun onIntent(intent: SavedIntent) {
        when (intent) {
            is SavedIntent.OpenGuide -> viewModelScope.launch {
                _effects.send(SavedUiEffect.OpenGuideDetail(intent.guide.id))
            }

            SavedIntent.NavigateToSolve -> viewModelScope.launch {
                _effects.send(SavedUiEffect.NavigateToSolve)
            }

            is SavedIntent.DeleteGuide -> deleteGuide(intent.id)
        }
    }

    private fun loadGuides() {
        viewModelScope.launch {
            getSavedGuidesUseCase()
                .catch { _uiState.value = SavedUiState.Empty }
                .collect { guides ->
                    if (guides.isEmpty()) {
                        _uiState.value = SavedUiState.Empty
                    } else {
                        _uiState.value = SavedUiState.Content(
                            preBundledGuides = guides.filter { it.isPreBundled },
                            userSavedGuides = guides.filter { !it.isPreBundled },
                            featuredGuide = guides.firstOrNull { it.isFeatured },
                            isOfflineMode = false
                        )
                    }
                }
        }
    }

    private fun buildPreBundledGuides(): List<SavedGuide> = listOf(
        guide(PreBundledGuideKey.TRASH_SORTING, Category.TRASH),
        guide(PreBundledGuideKey.WASHING_MACHINE, Category.APPLIANCE),
        guide(PreBundledGuideKey.SUBWAY_TMONEY, Category.TRANSPORT),
        guide(PreBundledGuideKey.GAS_STOVE, Category.APPLIANCE),
        guide(PreBundledGuideKey.MEDICAL_CLINICS, Category.MEDICAL),
        guide(PreBundledGuideKey.BUS_SYSTEM, Category.TRANSPORT),
        guide(PreBundledGuideKey.APARTMENT_INTERCOM, Category.APPLIANCE),
        guide(PreBundledGuideKey.ADDRESS_PEOPLE, Category.GENERAL),
        guide(PreBundledGuideKey.DAILY_PHRASES, Category.GENERAL),
        guide(PreBundledGuideKey.SIM_CARDS, Category.GENERAL),
        guide(PreBundledGuideKey.GETTING_AROUND, Category.TRANSPORT),
        guide(PreBundledGuideKey.WHERE_TO_STAY, Category.GENERAL)
    )

    private fun guide(
        key: PreBundledGuideKey,
        category: Category,
        featured: Boolean = false
    ) = SavedGuide(
        title = "",
        description = "",
        guideKey = key,
        category = category,
        solveResult = null,
        isPreBundled = true,
        isFeatured = featured,
        imageUri = null
    )

    private fun deleteGuide(id: Long) {
        // V2: wire to DeleteGuideUseCase
    }
}