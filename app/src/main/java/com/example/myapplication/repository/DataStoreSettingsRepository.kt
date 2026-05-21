package com.example.myapplication.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "innocence_settings"
)

class DataStoreSettingsRepository(
    private val context: Context
) : SettingsRepository {
    override val settings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        AppSettings(
            cardAlpha = (preferences[Keys.CardAlpha] ?: 0.92f).coerceIn(0.72f, 0.96f),
            animationsEnabled = preferences[Keys.AnimationsEnabled] ?: true,
            language = preferences[Keys.Language] ?: "中文",
            backgroundKey = preferences[Keys.BackgroundKey] ?: "meadow",
            customBackgroundUri = preferences[Keys.CustomBackgroundUri],
            customBackgroundOffsetX = (preferences[Keys.CustomBackgroundOffsetX] ?: 0f).coerceIn(-1f, 1f),
            customBackgroundOffsetY = (preferences[Keys.CustomBackgroundOffsetY] ?: 0f).coerceIn(-1f, 1f),
            customBackgroundBlur = (preferences[Keys.CustomBackgroundBlur] ?: 0f).coerceIn(0f, 28f),
            themeKey = preferences[Keys.ThemeKey] ?: "fresh",
            customThemeColor = preferences[Keys.CustomThemeColor] ?: 0xFF5F8D63.toInt(),
            hasSeededDefaults = preferences[Keys.HasSeededDefaults] ?: false
        )
    }

    override suspend fun setCardAlpha(value: Float) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.CardAlpha] = value.coerceIn(0.72f, 0.96f)
        }
    }

    override suspend fun setAnimationsEnabled(value: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.AnimationsEnabled] = value
        }
    }

    override suspend fun setLanguage(value: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.Language] = value
        }
    }

    override suspend fun setBackgroundKey(value: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.BackgroundKey] = value
        }
    }

    override suspend fun setCustomBackgroundUri(value: String?) {
        context.settingsDataStore.edit { preferences ->
            if (value.isNullOrBlank()) {
                preferences.remove(Keys.CustomBackgroundUri)
                preferences.remove(Keys.CustomBackgroundOffsetX)
                preferences.remove(Keys.CustomBackgroundOffsetY)
                preferences.remove(Keys.CustomBackgroundBlur)
            } else {
                preferences[Keys.CustomBackgroundUri] = value
                preferences[Keys.CustomBackgroundOffsetX] = 0f
                preferences[Keys.CustomBackgroundOffsetY] = 0f
            }
        }
    }

    override suspend fun setCustomBackgroundOffset(x: Float, y: Float) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.CustomBackgroundOffsetX] = x.coerceIn(-1f, 1f)
            preferences[Keys.CustomBackgroundOffsetY] = y.coerceIn(-1f, 1f)
        }
    }

    override suspend fun setCustomBackgroundBlur(value: Float) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.CustomBackgroundBlur] = value.coerceIn(0f, 28f)
        }
    }

    override suspend fun setThemeKey(value: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.ThemeKey] = value
        }
    }

    override suspend fun setCustomThemeColor(value: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.CustomThemeColor] = value
        }
    }

    override suspend fun setDefaultsSeeded() {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.HasSeededDefaults] = true
        }
    }

    private object Keys {
        val CardAlpha = floatPreferencesKey("card_alpha")
        val AnimationsEnabled = booleanPreferencesKey("animations_enabled")
        val Language = stringPreferencesKey("language")
        val BackgroundKey = stringPreferencesKey("background_key")
        val CustomBackgroundUri = stringPreferencesKey("custom_background_uri")
        val CustomBackgroundOffsetX = floatPreferencesKey("custom_background_offset_x")
        val CustomBackgroundOffsetY = floatPreferencesKey("custom_background_offset_y")
        val CustomBackgroundBlur = floatPreferencesKey("custom_background_blur")
        val ThemeKey = stringPreferencesKey("theme_key")
        val CustomThemeColor = intPreferencesKey("custom_theme_color")
        val HasSeededDefaults = booleanPreferencesKey("has_seeded_defaults")
    }
}
