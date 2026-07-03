package com.puri.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [HistoryEntity::class, SavedGuideEntity::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class PuriDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun savedGuideDao(): SavedGuideDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE history ADD COLUMN visibleTextsJson TEXT NOT NULL DEFAULT '[]'"
                )
                db.execSQL(
                    "ALTER TABLE history ADD COLUMN recommendedAction TEXT"
                )
                db.execSQL(
                    "ALTER TABLE saved_guides ADD COLUMN visibleTextsJson TEXT NOT NULL DEFAULT '[]'"
                )
                db.execSQL(
                    "ALTER TABLE saved_guides ADD COLUMN recommendedAction TEXT"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE saved_guides ADD COLUMN solveResult TEXT"
                )
            }
        }
    }
}