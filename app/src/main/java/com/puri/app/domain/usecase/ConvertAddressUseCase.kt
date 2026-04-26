package com.puri.app.domain.usecase

import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.domain.model.AddressResult
import com.puri.app.domain.repository.AddressRepository
import javax.inject.Inject

class ConvertAddressUseCase @Inject constructor(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(rawAddress: String): Resource<AddressResult> {
        if (rawAddress.isBlank()) return Resource.Error(PuriError.EmptyQuery)
        return repository.convertAddress(rawAddress.trim())
    }
}