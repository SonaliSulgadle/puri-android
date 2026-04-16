package com.puri.app.di

import android.content.Context
import androidx.room.Room
import com.puri.app.data.local.db.HistoryDao
import com.puri.app.data.local.db.PuriDatabase
import com.puri.app.data.local.db.SavedGuideDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePuriDatabase(@ApplicationContext context: Context): PuriDatabase =
        Room.databaseBuilder(
            context,
            PuriDatabase::class.java,
            "puri_database"
        )
            .addMigrations(PuriDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun provideHistoryDao(db: PuriDatabase): HistoryDao = db.historyDao()

    @Provides
    fun provideSavedGuideDao(db: PuriDatabase): SavedGuideDao = db.savedGuideDao()
}