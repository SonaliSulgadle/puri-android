package com.puri.app.domain.usecase

import com.puri.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailySolvesRemainingUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<Int> = preferencesRepository.dailySolvesRemaining
}