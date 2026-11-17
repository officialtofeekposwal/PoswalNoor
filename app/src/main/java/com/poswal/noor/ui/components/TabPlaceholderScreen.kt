package com.poswal.noor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens

@Composable
fun TabPlaceholderScreen(
    title: String,
    subtitle: String,
    onShowDesignShowcase: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = colors.primaryContainer,
                shape = NoorShapes.chip
            ) {
                Text(
                    text = "Poswal Noor",
                    style = typography.caption.copy(fontWeight = FontWeight.Bold),
                    color = colors.primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(NoorSpacing.l))

            Text(
                text = title,
                style = typography.display.copy(fontSize = 26.sp, fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(NoorSpacing.s))

            Text(
                text = subtitle,
                style = typography.body.copy(fontSize = 15.sp),
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )

            if (onShowDesignShowcase != null) {
                Spacer(modifier = Modifier.height(NoorSpacing.xl))
                OutlinedButton(
                    onClick = onShowDesignShowcase,
                    shape = NoorShapes.chip,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
                ) {
                    Text(text = "View Phase 1 Design Showcase")
                }
            }
        }
    }
}
