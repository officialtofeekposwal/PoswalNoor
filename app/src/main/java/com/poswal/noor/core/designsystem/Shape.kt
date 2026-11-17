package com.poswal.noor.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object NoorSpacing {
    val xs: Dp = 4.dp
    val s: Dp = 8.dp
    val m: Dp = 12.dp
    val l: Dp = 16.dp
    val xl: Dp = 20.dp       // Standard screen side padding
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp

    val minTouchTarget: Dp = 48.dp
}

object NoorShapes {
    val chip = RoundedCornerShape(12.dp)
    val card = RoundedCornerShape(20.dp)
    val sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val hero = RoundedCornerShape(28.dp)
    val pill = RoundedCornerShape(100.dp)
}
