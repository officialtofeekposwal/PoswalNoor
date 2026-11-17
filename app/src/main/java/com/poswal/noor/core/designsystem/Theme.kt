package com.poswal.noor.core.designsystem

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun NoorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkNoorColors else LightNoorColors
    val typography = NoorTypography()

    val materialColors = if (darkTheme) {
        darkColorScheme(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            primaryContainer = colors.primaryContainer,
            background = colors.background,
            surface = colors.surface,
            surfaceVariant = colors.surfaceElevated,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
            outline = colors.outline,
            error = colors.error
        )
    } else {
        lightColorScheme(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            primaryContainer = colors.primaryContainer,
            background = colors.background,
            surface = colors.surface,
            surfaceVariant = colors.surfaceElevated,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
            outline = colors.outline,
            error = colors.error
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colors.background.toArgb()
                window.navigationBarColor = colors.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalNoorColors provides colors,
        LocalNoorTypography provides typography,
        LocalReduceMotion provides reduceMotion
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            content = content
        )
    }
}

object NoorThemeTokens {
    val colors: NoorColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNoorColors.current

    val typography: NoorTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalNoorTypography.current

    val reduceMotion: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalReduceMotion.current
}
