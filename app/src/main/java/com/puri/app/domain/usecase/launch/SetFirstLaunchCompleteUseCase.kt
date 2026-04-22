package com.puri.app.domain.usecase.launch

import com.puri.app.domain.repository.PreferencesRepository
import javax.inject.Inject

class SetFirstLaunchCompleteUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke() = repository.setFirstLaunchComplete()
}