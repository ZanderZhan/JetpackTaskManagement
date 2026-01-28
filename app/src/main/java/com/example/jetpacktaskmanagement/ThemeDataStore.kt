package com.example.jetpacktaskmanagement

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeDataStore(private val context: Context) {
    companion object {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_DARK_THEME] ?: false
        }

    suspend fun setTheme(isDarkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_THEME] = isDarkTheme
        }
    }
}
