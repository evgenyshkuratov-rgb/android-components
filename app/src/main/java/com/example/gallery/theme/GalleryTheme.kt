package com.example.gallery.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF40B259),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    surfaceVariant = Color(0xFFF5F5F5),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF40B259),
    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFFFFFFF),
    background = Color(0xFF1A1A1A),
    onBackground = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF313131),
)

@Composable
fun GalleryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, content = content)
}
