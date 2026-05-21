package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = InnocencePrimary,
    onPrimary = Color.White,
    secondary = InnocenceAccent,
    onSecondary = InnocenceInk,
    tertiary = InnocenceWarm,
    onTertiary = InnocenceInk,
    background = InnocenceBackgroundTop,
    onBackground = InnocenceInk,
    surface = InnocenceCard,
    onSurface = InnocenceInk,
    surfaceVariant = InnocenceHighlight,
    onSurfaceVariant = InnocenceSubtleText,
    outline = InnocenceCardBorder
)

@Composable
fun InnocenceTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
