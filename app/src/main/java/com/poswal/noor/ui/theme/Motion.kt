package com.poswal.noor.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp

object NoorMotion {
    const val DurationFast = 150
    const val DurationMedium = 300
    const val DurationSlow = 500
    const val DurationFeedStagger = 60

    val EmphasizedEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val StandardEasing = FastOutSlowInEasing
    val DecelerateEasing = LinearOutSlowInEasing

    fun <T> fastTween() = tween<T>(durationMillis = DurationFast, easing = StandardEasing)
    fun <T> mediumTween() = tween<T>(durationMillis = DurationMedium, easing = EmphasizedEasing)
    fun <T> slowTween() = tween<T>(durationMillis = DurationSlow, easing = DecelerateEasing)
}

object NoorElevation {
    val Flat = 0.dp
    val Low = 2.dp
    val Medium = 4.dp
    val High = 8.dp
    val GlassModal = 12.dp
}
