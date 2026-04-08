package com.puri.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedGuideDao {

    @Query("SELECT * FROM saved_guides ORDER BY savedAt DESC")
    fun getAllSavedGuides(): Flow<List<SavedGuideEntity>>

    @Query("SELECT * FROM saved_guides WHERE isFeatured = 1 LIMIT 1")
    fun getFeaturedGuide(): Flow<SavedGuideEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuide(guide: SavedGuideEntity): Long

    @Query("DELETE FROM saved_guides WHERE id = :id")
    suspend fun deleteGuide(id: Long)

    @Query("DELETE FROM saved_guides WHERE historyItemId = :historyItemId")
    suspend fun deleteGuideByHistoryId(historyItemId: Long)
}