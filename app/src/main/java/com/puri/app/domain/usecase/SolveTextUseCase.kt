package com.puri.app.domain.usecase

import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.domain.model.HistoryItem
import com.puri.app.domain.model.SolveResult
import com.puri.app.domain.repository.HistoryRepository
import com.puri.app.domain.repository.PreferencesRepository
import com.puri.app.domain.repository.SolveRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SolveTextUseCase @Inject constructor(
    private val solveRepository: SolveRepository,
    private val historyRepository: HistoryRepository,
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(query: String): Resource<SolveResult> {
        if (query.isBlank()) {
            return Resource.Error(PuriError.EmptyQuery)
        }

        val remaining = preferencesRepository.dailySolvesRemaining.first()
        if (remaining <= 0) {
            return Resource.Error(PuriError.DailyLimitReached)
        }

        return when (val result = solveRepository.solveText(query)) {
            is Resource.Success -> {
                historyRepository.saveToHistory(
                    HistoryItem(solveResult = result.data)
                )
                preferencesRepository.decrementDailySolves()
                result
            }

            is Resource.Error -> result
            is Resource.Loading -> result
        }
    }
}