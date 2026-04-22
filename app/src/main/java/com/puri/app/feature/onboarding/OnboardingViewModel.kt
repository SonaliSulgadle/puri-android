package com.puri.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.domain.usecase.launch.SetFirstLaunchCompleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val setFirstLaunchCompleteUseCase: SetFirstLaunchCompleteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _effects = Channel<OnboardingUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            OnboardingIntent.NextPage -> {
                if (_uiState.value.isLastPage) finish()
                else _uiState.update { it.copy(currentPage = it.currentPage + 1) }
            }

            OnboardingIntent.PreviousPage -> {
                if (!_uiState.value.isFirstPage) {
                    _uiState.update { it.copy(currentPage = it.currentPage - 1) }
                }
            }

            OnboardingIntent.Skip -> finish()
            OnboardingIntent.Finish -> finish()
            is OnboardingIntent.SyncPage ->
                _uiState.update { it.copy(currentPage = intent.page) }
        }
    }

    private fun finish() {
        if (_uiState.value.isFinishing) return
        _uiState.update { it.copy(isFinishing = true) }
        viewModelScope.launch {
            setFirstLaunchCompleteUseCase()
            _effects.send(OnboardingUiEffect.NavigateToHome)
        }
    }
}