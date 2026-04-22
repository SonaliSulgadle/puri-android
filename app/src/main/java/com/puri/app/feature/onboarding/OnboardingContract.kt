package com.puri.app.feature.onboarding

sealed interface OnboardingIntent {
    data object NextPage : OnboardingIntent
    data object PreviousPage : OnboardingIntent
    data object Skip : OnboardingIntent
    data object Finish : OnboardingIntent
    data class SyncPage(val page: Int) : OnboardingIntent
}

data class OnboardingUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 3,
    val isFinishing: Boolean = false
) {
    val isLastPage: Boolean get() = currentPage == totalPages - 1
    val isFirstPage: Boolean get() = currentPage == 0
}

sealed interface OnboardingUiEffect {
    data object NavigateToHome : OnboardingUiEffect
}