package com.puri.app.domain.usecase

import com.puri.app.core.common.Resource
import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.repository.HistoryRepository
import com.puri.app.domain.repository.SavedGuidesRepository
import javax.inject.Inject

class SaveGuideUseCase @Inject constructor(
    private val savedGuidesRepository: SavedGuidesRepository,
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(
        guide: SavedGuide,
        historyItemId: Long? = null
    ): Resource<Unit> {
        val alreadySaved = savedGuidesRepository.existsByTitleAndCategory(
            title = guide.title,
            category = guide.category.name
        )
        return if (alreadySaved) {
            Resource.Success(Unit)
        } else {
            val result = savedGuidesRepository.saveGuide(guide)
            historyItemId?.let { historyRepository.markAsSaved(it) }
            result
        }
    }
}