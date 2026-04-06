package com.puri.app.domain.usecase

import com.puri.app.domain.model.HistoryItem
import com.puri.app.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke(): Flow<List<HistoryItem>> =
        historyRepository.getHistory()
}