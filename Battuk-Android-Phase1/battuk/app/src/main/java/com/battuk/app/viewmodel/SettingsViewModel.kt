package com.battuk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battuk.app.util.AppThemeMode
import com.battuk.app.util.PreferencesManager
import com.battuk.app.util.SoundManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Year

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val soundManager: SoundManager
) : ViewModel() {

    val themeMode: StateFlow<AppThemeMode> = preferencesManager.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeMode.SYSTEM)

    val soundOn: StateFlow<Boolean> = preferencesManager.soundOn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val activeYear: StateFlow<Int> = preferencesManager.activeYear
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Year.now().value)

    fun setTheme(mode: AppThemeMode) {
        viewModelScope.launch { preferencesManager.setThemeMode(mode) }
    }

    fun cycleTheme() {
        val next = when (themeMode.value) {
            AppThemeMode.SYSTEM -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.SYSTEM
        }
        setTheme(next)
    }

    fun setSound(on: Boolean) {
        soundManager.setSoundOn(on)
        viewModelScope.launch { preferencesManager.setSoundOn(on) }
    }

    fun setActiveYear(year: Int) {
        viewModelScope.launch { preferencesManager.setActiveYear(year) }
    }

    fun playClickIfEnabled() {
        soundManager.playClick()
    }
}
