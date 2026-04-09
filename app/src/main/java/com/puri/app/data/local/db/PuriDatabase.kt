package com.puri.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [HistoryEntity::class, SavedGuideEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class PuriDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun savedGuideDao(): SavedGuideDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Yet to implement
                // MIGRATION GUIDE — read before incrementing version:
                // 1. Increment version in @Database annotation
                // 2. Add MIGRATION_X_Y object here with the SQL
                // 3. Add it to addMigrations() in DatabaseModule
                // 4. Build project — Room generates new schema JSON in schemas/
                // 5. Write MigrationTest in androidTest/ verifying data survives
                // 6. Run on a device that had the previous version installed
            }
        }
    }
}