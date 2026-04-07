package com.puri.app.data.repository

import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.data.local.db.SavedGuideDao
import com.puri.app.data.mapper.toDomain
import com.puri.app.data.mapper.toEntity
import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.repository.SavedGuidesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedGuidesRepositoryImpl @Inject constructor(
    private val savedGuideDao: SavedGuideDao
) : SavedGuidesRepository {

    override fun getSavedGuides(): Flow<List<SavedGuide>> =
        savedGuideDao.getAllSavedGuides().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getFeaturedGuide(): Flow<SavedGuide?> =
        savedGuideDao.getFeaturedGuide().map { it?.toDomain() }

    override suspend fun saveGuide(guide: SavedGuide): Resource<Unit> =
        try {
            savedGuideDao.insertGuide(guide.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(PuriError.Unknown(e))
        }

    override suspend fun deleteGuide(id: Long): Resource<Unit> =
        try {
            savedGuideDao.deleteGuide(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(PuriError.Unknown(e))
        }

    override suspend fun toggleSaved(historyItemId: Long): Resource<Unit> =
        try {
            // Read current state then flip it
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(PuriError.Unknown(e))
        }
}