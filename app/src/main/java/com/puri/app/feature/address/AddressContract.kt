package com.puri.app.feature.address

import com.puri.app.domain.model.AddressResult

sealed interface AddressIntent {
    data class UpdateInput(val text: String) : AddressIntent
    data object Convert : AddressIntent
    data object ClearResult : AddressIntent
    data object DismissError : AddressIntent
}

data class AddressUiState(
    val input: String = "",
    val isLoading: Boolean = false,
    val result: AddressResult? = null,
    val showError: Boolean = false,
    val convertsRemaining: Int = 5
)

sealed interface AddressUiEffect {
    data object NavigateToResult : AddressUiEffect
    data class ShowSnackbar(val messageRes: Int) : AddressUiEffect
}