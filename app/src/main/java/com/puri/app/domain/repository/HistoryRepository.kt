package com.puri.app.domain.repository

import com.puri.app.core.common.Resource
import com.puri.app.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistory(): Flow<List<HistoryItem>>
    suspend fun saveToHistory(item: HistoryItem): Resource<Unit>
    suspend fun deleteHistoryItem(id: Long): Resource<Unit>
    suspend fun clearAllHistory(): Resource<Unit>
    fun getHistoryItem(id: Long): Flow<HistoryItem?>
    suspend fun clearAll()
}