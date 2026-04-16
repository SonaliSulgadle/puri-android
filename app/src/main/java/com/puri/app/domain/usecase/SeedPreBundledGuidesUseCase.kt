package com.puri.app.domain.usecase

import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.repository.SavedGuidesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SeedPreBundledGuidesUseCase @Inject constructor(
    private val repository: SavedGuidesRepository
) {
    suspend operator fun invoke(guides: List<SavedGuide>) {
        val existing = repository.getSavedGuides().first()
        if (existing.any { it.isPreBundled }) return
        guides.forEach { repository.saveGuide(it) }
    }
}