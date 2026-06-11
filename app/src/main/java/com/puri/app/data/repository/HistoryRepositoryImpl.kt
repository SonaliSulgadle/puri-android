package com.puri.app.data.repository

import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.data.local.db.HistoryDao
import com.puri.app.data.mapper.toDomain
import com.puri.app.data.mapper.toEntity
import com.puri.app.domain.model.HistoryItem
import com.puri.app.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {

    override fun getHistory(): Flow<List<HistoryItem>> =
        historyDao.getAllHistory()
            .map { entities -> entities.map { it.toDomain() } }
            .catch { e ->
                emit(emptyList())
                // Log to crash reporting in future
            }

    override fun getHistoryItem(id: Long): Flow<HistoryItem?> =
        historyDao.getHistoryItem(id).map { it?.toDomain() }

    override suspend fun saveToHistory(item: HistoryItem): Resource<Unit> =
        try {
            historyDao.insertHistoryItem(item.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(PuriError.Unknown(e))
        }

    override suspend fun deleteHistoryItem(id: Long): Resource<Unit> =
        try {
            historyDao.deleteHistoryItem(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(PuriError.Unknown(e))
        }

    override suspend fun clearAllHistory(): Resource<Unit> =
        try {
            historyDao.clearAllHistory()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(PuriError.Unknown(e))
        }

    override suspend fun clearAll() = historyDao.deleteAll()

    override suspend fun markAsSaved(historyItemId: Long) {
        historyDao.updateIsSaved(historyItemId)
    }
}