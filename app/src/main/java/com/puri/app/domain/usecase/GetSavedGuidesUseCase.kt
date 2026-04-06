package com.puri.app.domain.usecase

import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.repository.SavedGuidesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedGuidesUseCase @Inject constructor(
    private val savedGuidesRepository: SavedGuidesRepository
) {
    operator fun invoke(): Flow<List<SavedGuide>> =
        savedGuidesRepository.getSavedGuides()
}