package com.puri.app.feature.profile

sealed interface ProfileIntent {
    data object ClearHistory : ProfileIntent
    data object ClearSavedSolves : ProfileIntent
    data object DismissDialog : ProfileIntent
    data object ConfirmClear : ProfileIntent
}

data class ProfileUiState(
    val todaySolves: Int = 0,
    val dailyLimit: Int = 10,
    val totalSolves: Int = 0,
    val totalSaved: Int = 0,
    val appVersion: String = "",
    val pendingClearType: ClearType? = null,
    val isClearing: Boolean = false
)

enum class ClearType {
    HISTORY,
    SAVED_SOLVES
}

sealed interface ProfileUiEffect {
    data class ShowSnackbar(val messageRes: Int) : ProfileUiEffect
}