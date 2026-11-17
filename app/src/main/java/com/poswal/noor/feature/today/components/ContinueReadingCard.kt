package com.poswal.noor.feature.today.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmCard

@Composable
fun ContinueReadingCard(
    surahNumber: Int,
    surahName: String,
    ayahNumber: Int,
    totalVerses: Int,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    val progress = if (totalVerses > 0) {
        (ayahNumber.toFloat() / totalVerses.toFloat()).coerceIn(0.01f, 1.0f)
    } else 0.05f

    CalmCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onContinueClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(NoorSpacing.m))

                    Column {
                        Text(
                            text = "CONTINUE READING",
                            style = typography.label.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = colors.textSecondary
                        )
                        Text(
                            text = "$surahName · Ayah $ayahNumber",
                            style = typography.subtitle.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.textPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(NoorShapes.pill)
                        .background(colors.surfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Resume",
                            style = typography.label.copy(fontWeight = FontWeight.Bold),
                            color = colors.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(NoorShapes.pill),
                color = colors.primary,
                trackColor = colors.outline.copy(alpha = 0.35f),
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Surah #$surahNumber",
                    style = typography.caption.copy(fontSize = 11.sp),
                    color = colors.textSecondary
                )
                Text(
                    text = "$ayahNumber of $totalVerses ayahs (${(progress * 100).toInt()}%)",
                    style = typography.caption.copy(fontSize = 11.sp),
                    color = colors.textSecondary
                )
            }
        }
    }
}
