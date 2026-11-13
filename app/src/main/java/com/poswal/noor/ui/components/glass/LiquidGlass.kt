package com.poswal.noor.ui.components.glass

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration styling parameters for the LiquidGlass effect.
 */
@Immutable
data class LiquidGlassStyle(
    val blurRadius: Dp = 20.dp,
    val tintColor: Color = Color.Unspecified,
    val tintAlpha: Float = 0.55f,
    val borderWidth: Dp = 1.2.dp,
    val borderColors: List<Color> = emptyList(),
    val cornerRadius: Dp = 24.dp,
    val elevation: Dp = 8.dp,
    val enableSpecularHighlight: Boolean = true,
    val specularAlpha: Float = 0.45f
)

/**
 * Standard presets and sensible defaults for Liquid Glass components.
 */
object LiquidGlassDefaults {

    val DefaultCornerRadius = 24.dp
    val DefaultBlurRadius = 20.dp

    @Composable
    fun cardStyle(
        cornerRadius: Dp = 24.dp,
        elevation: Dp = 8.dp
    ): LiquidGlassStyle {
        val isDark = isSystemInDarkTheme()
        val tint = if (isDark) {
            Color(0xFF0F172A)
        } else {
            Color(0xFFFFFFFF)
        }
        val borderColors = if (isDark) {
            listOf(
                Color.White.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.12f),
                Color(0xFF10B981).copy(alpha = 0.20f),
                Color.Black.copy(alpha = 0.30f)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.75f),
                Color.White.copy(alpha = 0.30f),
                Color(0xFF10B981).copy(alpha = 0.15f),
                Color.White.copy(alpha = 0.10f)
            )
        }

        return LiquidGlassStyle(
            blurRadius = 22.dp,
            tintColor = tint,
            tintAlpha = if (isDark) 0.60f else 0.58f,
            borderWidth = 1.2.dp,
            borderColors = borderColors,
            cornerRadius = cornerRadius,
            elevation = elevation,
            enableSpecularHighlight = true,
            specularAlpha = if (isDark) 0.35f else 0.55f
        )
    }

    @Composable
    fun navBarStyle(): LiquidGlassStyle {
        val isDark = isSystemInDarkTheme()
        return LiquidGlassStyle(
            blurRadius = 25.dp,
            tintColor = if (isDark) Color(0xFF090D16) else Color(0xFFFFFFFF),
            tintAlpha = if (isDark) 0.75f else 0.72f,
            borderWidth = 1.2.dp,
            borderColors = listOf(
                Color.White.copy(alpha = if (isDark) 0.35f else 0.70f),
                Color(0xFF10B981).copy(alpha = 0.25f),
                Color.White.copy(alpha = 0.10f)
            ),
            cornerRadius = 32.dp,
            elevation = 16.dp,
            enableSpecularHighlight = true,
            specularAlpha = 0.40f
        )
    }

    @Composable
    fun buttonStyle(
        cornerRadius: Dp = 50.dp
    ): LiquidGlassStyle {
        val isDark = isSystemInDarkTheme()
        return LiquidGlassStyle(
            blurRadius = 16.dp,
            tintColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF),
            tintAlpha = if (isDark) 0.65f else 0.70f,
            borderWidth = 1.dp,
            borderColors = listOf(
                Color.White.copy(alpha = 0.60f),
                Color(0xFF10B981).copy(alpha = 0.30f),
                Color.White.copy(alpha = 0.15f)
            ),
            cornerRadius = cornerRadius,
            elevation = 4.dp,
            enableSpecularHighlight = true,
            specularAlpha = 0.50f
        )
    }

    @Composable
    fun sheetStyle(): LiquidGlassStyle {
        val isDark = isSystemInDarkTheme()
        return LiquidGlassStyle(
            blurRadius = 28.dp,
            tintColor = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
            tintAlpha = if (isDark) 0.80f else 0.78f,
            borderWidth = 1.2.dp,
            borderColors = listOf(
                Color.White.copy(alpha = if (isDark) 0.40f else 0.80f),
                Color.White.copy(alpha = 0.15f)
            ),
            cornerRadius = 32.dp,
            elevation = 20.dp,
            enableSpecularHighlight = true,
            specularAlpha = 0.50f
        )
    }
}

/**
 * Reusable Compose Modifier that applies the Apple-inspired Liquid Glass effect:
 * 1. Hardware blur (RenderEffect on Android 12+ / API 31+) with graceful tint fallback on older devices.
 * 2. Translucent milk/smoked glass base tint.
 * 3. Directional refraction border gradient (light catches top-left edge).
 * 4. Specular inner highlight simulating physical glass bevel thickness.
 * 5. Soft ambient drop shadow.
 */
fun Modifier.liquidGlass(
    style: LiquidGlassStyle,
    shape: Shape = RoundedCornerShape(style.cornerRadius)
): Modifier = composed {
    val density = LocalDensity.current
    val isDark = isSystemInDarkTheme()
    val supportsHardwareBlur = remember { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S }

    // Fallback: If device is pre-Android 12, increase opacity slightly for crisp readability
    val effectiveTintAlpha = remember(supportsHardwareBlur, style.tintAlpha) {
        if (supportsHardwareBlur) style.tintAlpha else (style.tintAlpha + 0.22f).coerceAtMost(0.92f)
    }

    val resolvedTintColor = if (style.tintColor == Color.Unspecified) {
        if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF)
    } else {
        style.tintColor
    }

    val borderBrush = remember(style.borderColors, isDark) {
        val colors = if (style.borderColors.isNotEmpty()) {
            style.borderColors
        } else {
            if (isDark) {
                listOf(
                    Color.White.copy(alpha = 0.35f),
                    Color(0xFF10B981).copy(alpha = 0.20f),
                    Color.White.copy(alpha = 0.08f)
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.75f),
                    Color(0xFF10B981).copy(alpha = 0.20f),
                    Color.White.copy(alpha = 0.15f)
                )
            }
        }
        Brush.linearGradient(
            colors = colors,
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

    this
        .shadow(
            elevation = style.elevation,
            shape = shape,
            clip = false,
            ambientColor = Color.Black.copy(alpha = 0.06f),
            spotColor = Color.Black.copy(alpha = 0.12f)
        )
        .clip(shape)
        // Layer 1: Hardware Blur on Android 12+ (RenderEffect)
        .then(
            if (supportsHardwareBlur) {
                Modifier.graphicsLayer {
                    val blurPx = with(density) { style.blurRadius.toPx() }
                    if (blurPx > 0f) {
                        renderEffect = RenderEffect.createBlurEffect(
                            blurPx,
                            blurPx,
                            Shader.TileMode.CLAMP
                        ).asComposeRenderEffect()
                    }
                }
            } else {
                Modifier
            }
        )
        // Layer 2: Translucent Tint Background
        .background(
            color = resolvedTintColor.copy(alpha = effectiveTintAlpha),
            shape = shape
        )
        // Layer 3: Specular Top Bevel Highlight (Physical Glass Gleam)
        .drawWithContent {
            drawContent()
            if (style.enableSpecularHighlight) {
                val highlightHeight = 1.8.dp.toPx()
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = style.specularAlpha),
                            Color(0xFF10B981).copy(alpha = style.specularAlpha * 0.5f),
                            Color.White.copy(alpha = style.specularAlpha),
                            Color.Transparent
                        )
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, highlightHeight)
                )
            }
        }
        // Layer 4: Refraction Border
        .border(
            width = style.borderWidth,
            brush = borderBrush,
            shape = shape
        )
}

/**
 * Convenient Composable Container implementing the Liquid Glass effect.
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    style: LiquidGlassStyle = LiquidGlassDefaults.cardStyle(),
    shape: Shape = RoundedCornerShape(style.cornerRadius),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.liquidGlass(style = style, shape = shape)
    ) {
        content()
    }
}
