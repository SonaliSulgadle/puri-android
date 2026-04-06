package com.puri.app.domain.usecase

import com.puri.app.core.common.Resource
import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.repository.SavedGuidesRepository
import javax.inject.Inject

class SaveGuideUseCase @Inject constructor(
    private val savedGuidesRepository: SavedGuidesRepository
) {
    suspend operator fun invoke(guide: SavedGuide): Resource<Unit> =
        savedGuidesRepository.saveGuide(guide)
}