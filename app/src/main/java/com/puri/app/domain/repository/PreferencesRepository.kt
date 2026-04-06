package com.puri.app.domain.repository

import com.puri.app.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val isFirstLaunch: Flow<Boolean>
    val appLanguage: Flow<AppLanguage>
    val dailySolvesRemaining: Flow<Int>
    val dailySolvesLimit: Int

    suspend fun setFirstLaunchComplete()
    suspend fun setAppLanguage(language: AppLanguage)
    suspend fun decrementDailySolves()
    suspend fun resetDailySolves()
}