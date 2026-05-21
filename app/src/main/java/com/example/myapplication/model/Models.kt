package com.example.myapplication.model

import androidx.compose.ui.graphics.Color

enum class Screen {
    Home,
    NewEntry,
    EntryDetail,
    Settings
}

enum class DetailEntrySource {
    Home,
    NewEntry
}

enum class DetailMode {
    Read,
    Edit
}

data class DiaryEntry(
    val id: Int,
    val date: String,
    val weekday: String,
    val mood: String,
    val songName: String,
    val artist: String,
    val lyricSnippet: String,
    val reflection: String,
    val photo: PhotoStyle,
    val coverUri: String? = null
)

data class EntryDraft(
    val date: String = "2026-05-13",
    val weekday: String = "星期三",
    val mood: String = "平静",
    val songName: String = "",
    val artist: String = "",
    val lyricSnippet: String = "",
    val reflection: String = "",
    val coverUri: String? = null
)

data class PhotoStyle(
    val base: Color,
    val glow: Color,
    val shadow: Color
)

data class AppSettings(
    val cardAlpha: Float = 0.92f,
    val animationsEnabled: Boolean = true,
    val language: String = "中文",
    val backgroundKey: String = "meadow",
    val customBackgroundUri: String? = null,
    val customBackgroundOffsetX: Float = 0f,
    val customBackgroundOffsetY: Float = 0f,
    val customBackgroundBlur: Float = 0f,
    val themeKey: String = "fresh",
    val customThemeColor: Int = 0xFF5F8D63.toInt(),
    val hasSeededDefaults: Boolean = false
)

val MeadowPhoto = PhotoStyle(
    base = Color(0xFFAFC8A7),
    glow = Color(0xFFF4F0D0),
    shadow = Color(0xFF395841)
)

val HarborPhoto = PhotoStyle(
    base = Color(0xFF7D9AA2),
    glow = Color(0xFFE0E7D8),
    shadow = Color(0xFF294555)
)

val NightPhoto = PhotoStyle(
    base = Color(0xFF566D7C),
    glow = Color(0xFFD8E8F1),
    shadow = Color(0xFF142535)
)

val FieldPhoto = PhotoStyle(
    base = Color(0xFFC7C89A),
    glow = Color(0xFFFFE7B8),
    shadow = Color(0xFF58643A)
)

val SkyPhoto = PhotoStyle(
    base = Color(0xFFB5C8D9),
    glow = Color(0xFFF9F5EA),
    shadow = Color(0xFF658299)
)

fun photoKeyOf(style: PhotoStyle): String = when (style) {
    MeadowPhoto -> "meadow"
    HarborPhoto -> "harbor"
    NightPhoto -> "night"
    FieldPhoto -> "field"
    SkyPhoto -> "sky"
    else -> "meadow"
}

fun photoStyleForKey(key: String): PhotoStyle = when (key) {
    "harbor" -> HarborPhoto
    "night" -> NightPhoto
    "field" -> FieldPhoto
    "sky" -> SkyPhoto
    else -> MeadowPhoto
}
