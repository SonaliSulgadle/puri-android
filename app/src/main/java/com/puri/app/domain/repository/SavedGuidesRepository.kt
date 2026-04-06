package com.puri.app.domain.repository

import com.puri.app.core.common.Resource
import com.puri.app.domain.model.SavedGuide
import kotlinx.coroutines.flow.Flow

interface SavedGuidesRepository {
    fun getSavedGuides(): Flow<List<SavedGuide>>
    fun getFeaturedGuide(): Flow<SavedGuide?>
    suspend fun saveGuide(guide: SavedGuide): Resource<Unit>
    suspend fun deleteGuide(id: Long): Resource<Unit>
    suspend fun toggleSaved(historyItemId: Long): Resource<Unit>
}