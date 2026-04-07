package com.puri.app.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.puri.app.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PuriPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        private val KEY_APP_LANGUAGE = stringPreferencesKey("app_language")
        private val KEY_DAILY_SOLVES = intPreferencesKey("daily_solves_remaining")
        private val KEY_LAST_RESET_DATE = longPreferencesKey("last_reset_date_epoch")

        const val DAILY_LIMIT = 10
    }

    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_FIRST_LAUNCH] ?: true
    }

    val appLanguage: Flow<AppLanguage> = dataStore.data.map { prefs ->
        AppLanguage.fromCode(prefs[KEY_APP_LANGUAGE] ?: AppLanguage.ENGLISH.code)
    }

    val dailySolvesRemaining: Flow<Int> = dataStore.data.map { prefs ->
        val lastReset = prefs[KEY_LAST_RESET_DATE] ?: 0L
        val today = LocalDate.now(ZoneId.of("Asia/Seoul")).toEpochDay()
        if (lastReset < today) DAILY_LIMIT
        else prefs[KEY_DAILY_SOLVES] ?: DAILY_LIMIT
    }

    suspend fun setFirstLaunchComplete() {
        dataStore.edit { it[KEY_FIRST_LAUNCH] = false }
    }

    suspend fun setAppLanguage(language: AppLanguage) {
        dataStore.edit { it[KEY_APP_LANGUAGE] = language.code }
    }

    suspend fun decrementDailySolves() {
        dataStore.edit { prefs ->
            val today = LocalDate.now(ZoneId.of("Asia/Seoul")).toEpochDay()
            val lastReset = prefs[KEY_LAST_RESET_DATE] ?: 0L

            if (lastReset < today) {
                // First solve of the day — reset counter then decrement
                prefs[KEY_DAILY_SOLVES] = DAILY_LIMIT - 1
                prefs[KEY_LAST_RESET_DATE] = today
            } else {
                val current = prefs[KEY_DAILY_SOLVES] ?: DAILY_LIMIT
                prefs[KEY_DAILY_SOLVES] = maxOf(0, current - 1)
            }
        }
    }

    suspend fun resetDailySolves() {
        dataStore.edit { prefs ->
            prefs[KEY_DAILY_SOLVES] = DAILY_LIMIT
            prefs[KEY_LAST_RESET_DATE] = LocalDate.now(ZoneId.of("Asia/Seoul")).toEpochDay()
        }
    }
}