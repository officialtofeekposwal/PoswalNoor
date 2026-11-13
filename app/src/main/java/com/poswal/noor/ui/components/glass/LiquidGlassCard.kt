package com.poswal.noor.ui.components.glass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Premium Apple-inspired Liquid Glass Card container.
 * Supports elevation, hardware blur, refractive borders, and optional click interaction.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    style: LiquidGlassStyle = LiquidGlassDefaults.cardStyle(),
    shape: Shape = RoundedCornerShape(style.cornerRadius),
    contentPadding: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(color = style.borderColors.firstOrNull() ?: androidx.compose.ui.graphics.Color.White),
            role = Role.Button,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .liquidGlass(style = style, shape = shape)
            .then(clickableModifier)
            .padding(contentPadding)
    ) {
        content()
    }
}
