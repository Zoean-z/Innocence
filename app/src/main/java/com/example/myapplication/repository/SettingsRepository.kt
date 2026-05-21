package com.example.myapplication.repository

import com.example.myapplication.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun setCardAlpha(value: Float)
    suspend fun setAnimationsEnabled(value: Boolean)
    suspend fun setLanguage(value: String)
    suspend fun setBackgroundKey(value: String)
    suspend fun setCustomBackgroundUri(value: String?)
    suspend fun setCustomBackgroundOffset(x: Float, y: Float)
    suspend fun setCustomBackgroundBlur(value: Float)
    suspend fun setThemeKey(value: String)
    suspend fun setCustomThemeColor(value: Int)
    suspend fun setDefaultsSeeded()
}
