package com.example.dashboard.ui.theme.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dashboard.data.ThemeDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val themeDataStore = ThemeDataStore(application)


    val isDarkTheme: StateFlow<Boolean> = themeDataStore.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )


    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            themeDataStore.setDarkTheme(isDark)
        }
    }
}