package com.puri.app.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.puri.app.core.util.DateTimeUtils.todayEpochDay
import com.puri.app.domain.model.AppLanguage
import com.puri.app.domain.model.DailyLimits
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PuriPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        private val KEY_APP_LANGUAGE = stringPreferencesKey("app_language")
        private val DAILY_SOLVES_REMAINING = intPreferencesKey("daily_solves_remaining")
        private val DAILY_SOLVES_RESET_DATE = longPreferencesKey("last_reset_date_epoch")
        val ADDRESS_CONVERTS_REMAINING = intPreferencesKey("address_converts_remaining")
        val ADDRESS_CONVERTS_RESET_DATE = longPreferencesKey("address_converts_reset_date")
        const val DAILY_LIMIT = 10
    }

    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_FIRST_LAUNCH] ?: true
    }

    val appLanguage: Flow<AppLanguage> = dataStore.data.map { prefs ->
        AppLanguage.fromCode(prefs[KEY_APP_LANGUAGE] ?: AppLanguage.ENGLISH.code)
    }

    val dailySolvesRemaining: Flow<Int> = dataStore.data
        .map { prefs ->
            val resetDate = prefs[DAILY_SOLVES_RESET_DATE] ?: 0L
            val todayEpoch = todayEpochDay()
            if (resetDate < todayEpoch) {
                DailyLimits.SNAP_AND_SOLVE
            } else {
                prefs[DAILY_SOLVES_REMAINING] ?: DailyLimits.SNAP_AND_SOLVE
            }
        }

    suspend fun setFirstLaunchComplete() {
        dataStore.edit { it[KEY_FIRST_LAUNCH] = false }
    }

    suspend fun setAppLanguage(language: AppLanguage) {
        dataStore.edit { it[KEY_APP_LANGUAGE] = language.code }
    }

    suspend fun decrementDailySolves() {
        dataStore.edit { prefs ->
            val todayEpoch = todayEpochDay()
            val resetDate = prefs[DAILY_SOLVES_RESET_DATE] ?: 0L

            val current = if (resetDate < todayEpoch) {
                prefs[DAILY_SOLVES_RESET_DATE] = todayEpoch
                DailyLimits.SNAP_AND_SOLVE
            } else {
                prefs[DAILY_SOLVES_REMAINING] ?: DailyLimits.SNAP_AND_SOLVE
            }

            prefs[DAILY_SOLVES_REMAINING] = (current - 1).coerceAtLeast(0)
        }
    }

    val addressConvertsRemaining: Flow<Int> = dataStore.data
        .map { prefs ->
            val resetDate = prefs[ADDRESS_CONVERTS_RESET_DATE] ?: 0L
            val todayEpoch = todayEpochDay()
            if (resetDate < todayEpoch) {
                DailyLimits.ADDRESS_CONVERT  // new day — return max
            } else {
                prefs[ADDRESS_CONVERTS_REMAINING] ?: DailyLimits.ADDRESS_CONVERT
            }
        }

    suspend fun decrementAddressConverts() {
        dataStore.edit { prefs ->
            val todayEpoch = todayEpochDay()
            val resetDate = prefs[ADDRESS_CONVERTS_RESET_DATE] ?: 0L

            val current = if (resetDate < todayEpoch) {
                prefs[ADDRESS_CONVERTS_RESET_DATE] = todayEpoch
                DailyLimits.ADDRESS_CONVERT
            } else {
                prefs[ADDRESS_CONVERTS_REMAINING] ?: DailyLimits.ADDRESS_CONVERT
            }

            prefs[ADDRESS_CONVERTS_REMAINING] = (current - 1).coerceAtLeast(0)
        }
    }
}