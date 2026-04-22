package com.puri.app.domain.usecase.launch

import com.puri.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFirstLaunchUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isFirstLaunch
}