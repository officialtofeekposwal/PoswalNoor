package com.poswal.noor.core.designsystem

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.compositionLocalOf

object NoorMotion {
    const val DURATION_MICRO: Int = 120
    const val DURATION_STANDARD: Int = 250
    const val DURATION_EMPHASIZED: Int = 400

    val EasingStandard: Easing = FastOutSlowInEasing

    fun <T> standardTween(durationMillis: Int = DURATION_STANDARD): TweenSpec<T> {
        return tween(durationMillis = durationMillis, easing = EasingStandard)
    }

    fun <T> springDefault(): SpringSpec<T> {
        return spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessMediumLow
        )
    }

    fun <T> responsiveSpec(reduceMotion: Boolean, normalSpec: AnimationSpec<T>): AnimationSpec<T> {
        return if (reduceMotion) {
            tween(durationMillis = 0)
        } else {
            normalSpec
        }
    }
}

val LocalReduceMotion = compositionLocalOf { false }
