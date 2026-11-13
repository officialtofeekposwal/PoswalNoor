package com.poswal.noor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = NoorEmerald,
    onPrimary = NoorIvory,
    secondary = NoorGold,
    onSecondary = NoorEmeraldDeep,
    background = NoorIvory,
    onBackground = NoorEmeraldDeep,
    surface = NoorIvory,
    onSurface = NoorEmeraldDeep,
    surfaceVariant = NoorIvoryVariant,
    onSurfaceVariant = NoorEmeraldDeep,
    outline = NoorGoldMuted
)

private val DarkColorScheme = darkColorScheme(
    primary = NoorGold,
    onPrimary = NoorEmeraldDeep,
    secondary = NoorEmerald,
    onSecondary = NoorIvory,
    background = NoorEmeraldDeep,
    onBackground = NoorIvory,
    surface = NoorEmeraldDeep,
    onSurface = NoorIvory,
    surfaceVariant = NoorEmeraldVariant,
    onSurfaceVariant = NoorIvory,
    outline = NoorGold
)

@Composable
fun NoorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NoorTypography,
        content = content
    )
}
