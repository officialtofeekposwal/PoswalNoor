package com.poswal.noor.feature.quran.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmCard
import com.poswal.noor.feature.quran.model.JuzItem

@Composable
fun JuzCard(
    juz: JuzItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    CalmCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NoorSpacing.l, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Juz Number Badge + Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.surfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = juz.number.toString(),
                        style = typography.subtitle.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(NoorSpacing.m))

                Column {
                    Text(
                        text = "Juz ${juz.number} · ${juz.nameEnglish}",
                        style = typography.subtitle.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Starts at ${juz.startSurahName} ${juz.startSurahNumber}:${juz.startAyah}",
                        style = typography.caption.copy(fontSize = 12.sp),
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${juz.totalVerses} Ayahs total",
                        style = typography.caption.copy(fontSize = 11.sp),
                        color = colors.primary
                    )
                }
            }

            // Right: Arabic Calligraphy (Amiri Quran)
            Text(
                text = juz.nameArabic,
                style = typography.arabicQuran.copy(
                    fontSize = 20.sp,
                    lineHeight = 26.sp
                ),
                color = colors.accent,
                textAlign = TextAlign.Right
            )
        }
    }
}
