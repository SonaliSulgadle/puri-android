package com.puri.app.fake

import com.puri.app.core.common.Resource
import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.repository.SavedGuidesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSavedGuidesRepository : SavedGuidesRepository {

    private val guides = mutableListOf<SavedGuide>()
    private val flow = MutableStateFlow<List<SavedGuide>>(emptyList())

    override fun getSavedGuides(): Flow<List<SavedGuide>> = flow

    override fun getFeaturedGuide(): Flow<SavedGuide?> =
        MutableStateFlow(guides.firstOrNull { it.isFeatured })

    override suspend fun saveGuide(guide: SavedGuide): Resource<Unit> {
        guides.add(guide)
        flow.value = guides.toList()
        return Resource.Success(Unit)
    }

    override suspend fun deleteGuide(id: Long): Resource<Unit> {
        guides.removeIf { it.id == id }
        flow.value = guides.toList()
        return Resource.Success(Unit)
    }

    override suspend fun toggleSaved(historyItemId: Long): Resource<Unit> {
        return Resource.Success(Unit)
    }
}