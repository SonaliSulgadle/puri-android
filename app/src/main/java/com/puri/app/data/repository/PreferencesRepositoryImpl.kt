package com.puri.app.data.repository

import com.puri.app.data.local.datastore.PuriPreferences
import com.puri.app.domain.model.AppLanguage
import com.puri.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val puriPreferences: PuriPreferences
) : PreferencesRepository {

    override val isFirstLaunch: Flow<Boolean> = puriPreferences.isFirstLaunch
    override val appLanguage: Flow<AppLanguage> = puriPreferences.appLanguage
    override val dailySolvesRemaining: Flow<Int> = puriPreferences.dailySolvesRemaining
    override val dailySolvesLimit: Int = PuriPreferences.DAILY_LIMIT

    override suspend fun setFirstLaunchComplete() = puriPreferences.setFirstLaunchComplete()
    override suspend fun setAppLanguage(language: AppLanguage) =
        puriPreferences.setAppLanguage(language)

    override suspend fun decrementDailySolves() = puriPreferences.decrementDailySolves()
    override suspend fun resetDailySolves() = puriPreferences.resetDailySolves()
}