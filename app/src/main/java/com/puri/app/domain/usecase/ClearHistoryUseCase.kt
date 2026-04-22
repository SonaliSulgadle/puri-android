package com.puri.app.domain.usecase

import com.puri.app.domain.repository.HistoryRepository
import javax.inject.Inject

class ClearHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke() = repository.clearAll()
}