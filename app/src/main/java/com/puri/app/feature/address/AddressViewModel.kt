package com.puri.app.feature.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.R
import com.puri.app.core.common.Resource
import com.puri.app.domain.usecase.ConvertAddressUseCase
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
class AddressViewModel @Inject constructor(
    private val convertAddressUseCase: ConvertAddressUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState: StateFlow<AddressUiState> = _uiState.asStateFlow()

    private val _effects = Channel<AddressUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: AddressIntent) {
        when (intent) {
            is AddressIntent.UpdateInput -> _uiState.update {
                it.copy(input = intent.text)
            }

            AddressIntent.Convert -> convert()
            AddressIntent.ClearResult -> _uiState.update {
                it.copy(result = null, showError = false)
            }

            AddressIntent.DismissError -> _uiState.update {
                it.copy(showError = false)
            }
        }
    }

    private fun convert() {
        val input = _uiState.value.input.trim()
        if (input.isBlank()) {
            viewModelScope.launch {
                _effects.send(AddressUiEffect.ShowSnackbar(R.string.error_empty_query))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showError = false) }

            when (val result = convertAddressUseCase(input)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            result = result.data
                        )
                    }
                    _effects.send(AddressUiEffect.NavigateToResult)
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, showError = true)
                    }
                    _effects.send(
                        AddressUiEffect.ShowSnackbar(R.string.error_unknown)
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }
}