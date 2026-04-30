package com.puri.app.fake

import com.puri.app.domain.model.AppLanguage
import com.puri.app.domain.model.DailyLimits
import com.puri.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePreferencesRepository : PreferencesRepository {

    private val _isFirstLaunch = MutableStateFlow(true)
    private val _dailySolvesRemaining = MutableStateFlow(DailyLimits.SNAP_AND_SOLVE)
    private val _addressConvertsRemaining = MutableStateFlow(DailyLimits.ADDRESS_CONVERT)
    private val _appLanguage = MutableStateFlow(AppLanguage.ENGLISH)

    override val isFirstLaunch: Flow<Boolean> =
        _isFirstLaunch.asStateFlow()

    override val dailySolvesRemaining: Flow<Int> =
        _dailySolvesRemaining.asStateFlow()
    override val dailySolvesLimit: Int
        get() = DailyLimits.SNAP_AND_SOLVE

    override val addressConvertsRemaining: Flow<Int> =
        _addressConvertsRemaining.asStateFlow()

    override val appLanguage: Flow<AppLanguage> =
        _appLanguage.asStateFlow()

    // Tracking for assertions
    var firstLaunchCompleteCallCount = 0
    var dailySolveDecrementCount = 0
    var addressConvertDecrementCount = 0

    override suspend fun setFirstLaunchComplete() {
        firstLaunchCompleteCallCount++
        _isFirstLaunch.value = false
    }

    override suspend fun decrementDailySolves() {
        dailySolveDecrementCount++
        _dailySolvesRemaining.value =
            (_dailySolvesRemaining.value - 1).coerceAtLeast(0)
    }

    override suspend fun decrementAddressConverts() {
        addressConvertDecrementCount++
        _addressConvertsRemaining.value =
            (_addressConvertsRemaining.value - 1).coerceAtLeast(0)
    }

    override suspend fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
    }

    // Test helpers
    fun setDailySolvesRemaining(count: Int) {
        _dailySolvesRemaining.value = count
    }

    fun setAddressConvertsRemaining(count: Int) {
        _addressConvertsRemaining.value = count
    }

    fun setIsFirstLaunch(value: Boolean) {
        _isFirstLaunch.value = value
    }
}