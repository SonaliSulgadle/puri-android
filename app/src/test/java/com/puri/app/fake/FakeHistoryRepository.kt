package com.puri.app.fake

import com.puri.app.core.common.Resource
import com.puri.app.domain.model.HistoryItem
import com.puri.app.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeHistoryRepository : HistoryRepository {

    val savedItems = mutableListOf<HistoryItem>()
    private val flow = MutableStateFlow<List<HistoryItem>>(emptyList())

    override fun getHistory(): Flow<List<HistoryItem>> = flow

    override suspend fun saveToHistory(item: HistoryItem): Resource<Unit> {
        savedItems.add(item)
        flow.value = savedItems.toList()
        return Resource.Success(Unit)
    }

    override suspend fun deleteHistoryItem(id: Long): Resource<Unit> {
        savedItems.removeIf { it.id == id }
        flow.value = savedItems.toList()
        return Resource.Success(Unit)
    }

    override suspend fun clearAllHistory(): Resource<Unit> {
        savedItems.clear()
        flow.value = emptyList()
        return Resource.Success(Unit)
    }

    override fun getHistoryItem(id: Long): Flow<HistoryItem?> =
        MutableStateFlow(savedItems.find { it.id == id })
}