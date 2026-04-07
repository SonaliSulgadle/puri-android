package com.puri.app.domain.usecase

import android.graphics.Bitmap
import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.domain.model.HistoryItem
import com.puri.app.domain.model.SolveResult
import com.puri.app.domain.repository.HistoryRepository
import com.puri.app.domain.repository.PreferencesRepository
import com.puri.app.domain.repository.SolveRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SolveImageUseCase @Inject constructor(
    private val solveRepository: SolveRepository,
    private val historyRepository: HistoryRepository,
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(
        bitmap: Bitmap,
        additionalContext: String? = null,
        imageUri: String?
    ): Resource<SolveResult> {
        // To check daily limit before making API call
        val remaining = preferencesRepository.dailySolvesRemaining.first()
        if (remaining <= 0) {
            return Resource.Error(PuriError.DailyLimitReached)
        }

        return when (val result = solveRepository.solveImage(bitmap, additionalContext, imageUri)) {
            is Resource.Success -> {
                // Auto-save to history on every successful solve
                historyRepository.saveToHistory(
                    HistoryItem(solveResult = result.data)
                )
                // Decrement the daily counter
                preferencesRepository.decrementDailySolves()
                result
            }

            is Resource.Error -> result
            is Resource.Loading -> result
        }
    }
}