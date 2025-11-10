package com.example.dashboard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeDataStore(private val context: Context) {

    companion object {
        val IS_DARK_THEME_KEY = booleanPreferencesKey("is_dark_theme")
    }


    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_DARK_THEME_KEY] ?: false
        }


    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { settings ->
            settings[IS_DARK_THEME_KEY] = isDark
        }
    }
}