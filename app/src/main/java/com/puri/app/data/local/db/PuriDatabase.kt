package com.puri.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [HistoryEntity::class, SavedGuideEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class PuriDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun savedGuideDao(): SavedGuideDao
}