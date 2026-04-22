package com.puri.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE id = :id")
    fun getHistoryItem(id: Long): Flow<HistoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItem(item: HistoryEntity): Long

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistoryItem(id: Long)

    @Query("DELETE FROM history")
    suspend fun clearAllHistory()

    @Query("UPDATE history SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: Long, isSaved: Boolean)

    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getHistoryItemOnce(id: Long): HistoryEntity?

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}