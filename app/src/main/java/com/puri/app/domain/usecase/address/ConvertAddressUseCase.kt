package com.puri.app.domain.usecase

import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.domain.model.AddressResult
import com.puri.app.domain.repository.AddressRepository
import com.puri.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ConvertAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository,
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(rawAddress: String): Resource<AddressResult> {
        if (rawAddress.isBlank()) {
            return Resource.Error(PuriError.EmptyQuery)
        }

        // Check address-specific daily limit (separate from solve limit)
        val remaining = preferencesRepository.addressConvertsRemaining.first()
        if (remaining <= 0) {
            return Resource.Error(PuriError.AddressLimitReached)
        }

        val result = addressRepository.convertAddress(rawAddress.trim())

        // Decrement only on success — failed attempts don't count
        if (result is Resource.Success) {
            preferencesRepository.decrementAddressConverts()
        }

        return result
    }
}