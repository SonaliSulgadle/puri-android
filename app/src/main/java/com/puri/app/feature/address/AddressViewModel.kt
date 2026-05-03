package com.puri.app.feature.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.R
import com.puri.app.core.analytics.Analytics
import com.puri.app.core.analytics.PuriEvent
import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.domain.usecase.ConvertAddressUseCase
import com.puri.app.domain.usecase.address.GetAddressConvertsRemainingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val convertAddressUseCase: ConvertAddressUseCase,
    private val getAddressConvertsRemainingUseCase: GetAddressConvertsRemainingUseCase,
    private val analytics: Analytics
) : ViewModel() {

    private var convertJob: Job? = null

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState: StateFlow<AddressUiState> = _uiState.asStateFlow()

    private val _effects = Channel<AddressUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            getAddressConvertsRemainingUseCase().collect { remaining ->
                _uiState.update { it.copy(convertsRemaining = remaining) }
            }
        }
    }

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
                _effects.send(AddressUiEffect.ShowSnackbar(R.string.error_empty_address))
            }
            return
        }
        convertJob?.cancel()

        convertJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showError = false) }

            when (val result = convertAddressUseCase(input)) {
                is Resource.Success -> {
                    analytics.log(
                        PuriEvent.AddressConverted(
                            addressType = result.data.addressType.name.lowercase(),
                            confidence = result.data.confidence.name.lowercase(),
                            hasDetail = result.data.locationDetail != null
                        )
                    )
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            result = result.data
                        )
                    }
                    _effects.send(AddressUiEffect.NavigateToResult)
                }

                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    val messageRes = when (result.error) {
                        PuriError.AddressLimitReached ->
                            R.string.error_address_limit_reached

                        PuriError.DailyLimitReached ->
                            R.string.error_daily_limit_reached

                        else ->
                            R.string.error_unknown
                    }
                    _effects.send(AddressUiEffect.ShowSnackbar(messageRes))
                }

                Resource.Loading -> Unit
            }
        }
    }
}