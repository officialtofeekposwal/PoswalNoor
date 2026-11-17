package com.poswal.noor.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class NoorColors(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val outline: Color,
    val error: Color,
    val isDark: Boolean
)

val LightNoorColors = NoorColors(
    background = Color(0xFFFAF7F0),        // Ivory
    surface = Color(0xFFFFFFFF),           // Cards
    surfaceElevated = Color(0xFFF3EFE5),   // Elevated
    primary = Color(0xFF0F6B4F),           // Emerald
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDDEFE7),
    accent = Color(0xFFB8902F),            // Gold
    textPrimary = Color(0xFF1B2A24),
    textSecondary = Color(0xFF5E6E66),
    outline = Color(0xFFE4DECF),           // Divider
    error = Color(0xFFB3261E),
    isDark = false
)

val DarkNoorColors = NoorColors(
    background = Color(0xFF0B0F0E),        // OLED-friendly near black
    surface = Color(0xFF141A18),           // Cards
    surfaceElevated = Color(0xFF1B2421),   // Elevated
    primary = Color(0xFF3DBE8F),           // Emerald light
    onPrimary = Color(0xFF06150F),
    primaryContainer = Color(0xFF14392D),
    accent = Color(0xFFE0B95C),            // Gold light
    textPrimary = Color(0xFFEDF2EF),
    textSecondary = Color(0xFF9FB0A8),
    outline = Color(0xFF26312D),           // Divider
    error = Color(0xFFF2B8B5),
    isDark = true
)

val LocalNoorColors = staticCompositionLocalOf { LightNoorColors }
