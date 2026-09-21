package com.battuk.app.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import java.time.Year

private val Context.dataStore by preferencesDataStore(name = "battuk_settings")

enum class AppThemeMode { LIGHT, DARK, SYSTEM }

class PreferencesManager(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SOUND_ON = booleanPreferencesKey("sound_on")
        val ACTIVE_YEAR = intPreferencesKey("active_year")
    }

    val themeMode = context.dataStore.data.map { prefs ->
        runCatching { AppThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: AppThemeMode.SYSTEM.name) }
            .getOrDefault(AppThemeMode.SYSTEM)
    }

    val soundOn = context.dataStore.data.map { prefs -> prefs[Keys.SOUND_ON] ?: true }

    val activeYear = context.dataStore.data.map { prefs ->
        prefs[Keys.ACTIVE_YEAR] ?: Year.now().value
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setSoundOn(on: Boolean) {
        context.dataStore.edit { it[Keys.SOUND_ON] = on }
    }

    suspend fun setActiveYear(year: Int) {
        context.dataStore.edit { it[Keys.ACTIVE_YEAR] = year }
    }
}
