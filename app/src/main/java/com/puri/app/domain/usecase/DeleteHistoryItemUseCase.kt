package com.puri.app.domain.usecase

import com.puri.app.core.common.Resource
import com.puri.app.domain.repository.HistoryRepository
import javax.inject.Inject

class DeleteHistoryItemUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(id: Long): Resource<Unit> =
        historyRepository.deleteHistoryItem(id)
}