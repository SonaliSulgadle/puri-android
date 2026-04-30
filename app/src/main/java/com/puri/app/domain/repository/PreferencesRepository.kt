package com.puri.app.domain.repository

import com.puri.app.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val isFirstLaunch: Flow<Boolean>
    val appLanguage: Flow<AppLanguage> // V2
    val dailySolvesRemaining: Flow<Int>
    val dailySolvesLimit: Int
    val addressConvertsRemaining: Flow<Int>

    suspend fun setFirstLaunchComplete()
    suspend fun setAppLanguage(language: AppLanguage) // V2
    suspend fun decrementDailySolves()
    suspend fun decrementAddressConverts()
}