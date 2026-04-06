package com.puri.app.fake

import com.puri.app.domain.model.AppLanguage
import com.puri.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePreferencesRepository : PreferencesRepository {

    private val _dailySolvesRemaining = MutableStateFlow(10)
    private val _isFirstLaunch = MutableStateFlow(true)
    private val _appLanguage = MutableStateFlow(AppLanguage.ENGLISH)

    override val dailySolvesRemaining: Flow<Int> = _dailySolvesRemaining
    override val isFirstLaunch: Flow<Boolean> = _isFirstLaunch
    override val appLanguage: Flow<AppLanguage> = _appLanguage
    override val dailySolvesLimit: Int = 10

    override suspend fun setFirstLaunchComplete() {
        _isFirstLaunch.value = false
    }

    override suspend fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
    }

    override suspend fun decrementDailySolves() {
        _dailySolvesRemaining.value -= 1
    }

    override suspend fun resetDailySolves() {
        _dailySolvesRemaining.value = dailySolvesLimit
    }

    // Test helper — set remaining directly
    fun setDailySolvesRemaining(count: Int) {
        _dailySolvesRemaining.value = count
    }
}