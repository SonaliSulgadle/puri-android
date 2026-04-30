package com.puri.app.domain.usecase.address

import com.puri.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAddressConvertsRemainingUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    operator fun invoke(): Flow<Int> = repository.addressConvertsRemaining
}