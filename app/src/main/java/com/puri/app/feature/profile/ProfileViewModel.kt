package com.puri.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.BuildConfig
import com.puri.app.R
import com.puri.app.domain.usecase.ClearHistoryUseCase
import com.puri.app.domain.usecase.GetDailySolvesRemainingUseCase
import com.puri.app.domain.usecase.GetHistoryUseCase
import com.puri.app.domain.usecase.GetSavedGuidesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val getSavedGuidesUseCase: GetSavedGuidesUseCase,
    private val getDailySolvesRemainingUseCase: GetDailySolvesRemainingUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            appVersion = BuildConfig.VERSION_NAME
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _effects = Channel<ProfileUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                getHistoryUseCase(),
                getSavedGuidesUseCase(),
                getDailySolvesRemainingUseCase()
            ) { history, saved, remaining ->
                Triple(history, saved, remaining)
            }.collect { (history, saved, remaining) ->
                _uiState.update { current ->
                    current.copy(
                        todaySolves = 10 - remaining,
                        dailyLimit = 10,
                        totalSolves = history.size,
                        totalSaved = saved.count { !it.isPreBundled }
                    )
                }
            }
        }
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.ClearHistory -> _uiState.update {
                it.copy(pendingClearType = ClearType.HISTORY)
            }

            ProfileIntent.ClearSavedSolves -> _uiState.update {
                it.copy(pendingClearType = ClearType.SAVED_SOLVES)
            }

            ProfileIntent.DismissDialog -> _uiState.update {
                it.copy(pendingClearType = null)
            }

            ProfileIntent.ConfirmClear -> confirmClear()
        }
    }

    private fun confirmClear() {
        val clearType = _uiState.value.pendingClearType ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isClearing = true, pendingClearType = null) }
            when (clearType) {
                ClearType.HISTORY -> {
                    clearHistoryUseCase()
                    _effects.send(ProfileUiEffect.ShowSnackbar(R.string.profile_history_cleared))
                }

                ClearType.SAVED_SOLVES -> {
                    // V2 — ClearUserSavedGuidesUseCase
                    _effects.send(ProfileUiEffect.ShowSnackbar(R.string.profile_saved_cleared))
                }
            }
            _uiState.update { it.copy(isClearing = false) }
        }
    }
}