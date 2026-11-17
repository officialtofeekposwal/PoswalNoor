package com.poswal.noor.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = NoorShapes.chip,
    baseColor: Color = NoorThemeTokens.colors.surfaceElevated,
    highlightColor: Color = NoorThemeTokens.colors.outline.copy(alpha = 0.4f)
) {
    val reduceMotion = NoorThemeTokens.reduceMotion

    val brush = if (reduceMotion) {
        Brush.linearGradient(listOf(baseColor, baseColor))
    } else {
        val transition = rememberInfiniteTransition(label = "shimmerTransition")
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmerTranslate"
        )
        Brush.linearGradient(
            colors = listOf(baseColor, highlightColor, baseColor),
            start = Offset.Zero,
            end = Offset(x = translateAnim, y = translateAnim)
        )
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun CalmShimmerCard(
    modifier: Modifier = Modifier,
    height: Dp = 180.dp
) {
    CalmCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(18.dp)
            )
            Spacer(modifier = Modifier.height(NoorSpacing.m))
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
            )
            Spacer(modifier = Modifier.height(NoorSpacing.s))
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(20.dp)
            )
            Spacer(modifier = Modifier.height(NoorSpacing.l))
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
            )
        }
    }
}
