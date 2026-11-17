package com.poswal.noor.feature.quran.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmCard
import com.poswal.noor.data.local.entity.SurahEntity

@Composable
fun SurahCard(
    surah: SurahEntity,
    isLastRead: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    CalmCard(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isLastRead) Modifier.border(1.5.dp, colors.primary, NoorShapes.card)
                else Modifier
            ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NoorSpacing.l, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Star-shaped/Geometric Number Badge + Surah Metadata
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Number Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isLastRead) colors.primary else colors.surfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = surah.number.toString(),
                        style = typography.subtitle.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isLastRead) colors.onPrimary else colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(NoorSpacing.m))

                // English Name, Meaning & Metadata
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = surah.nameEnglish,
                            style = typography.subtitle.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = colors.textPrimary
                        )

                        if (isLastRead) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = colors.primaryContainer,
                                shape = NoorShapes.chip
                            ) {
                                Text(
                                    text = "Continue",
                                    style = typography.caption.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = colors.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = surah.nameTranslation,
                        style = typography.caption.copy(fontSize = 12.sp),
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Revelation Tag (Meccan / Medinan) + Verses Count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = colors.surfaceElevated,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = surah.revelationType.uppercase(),
                                style = typography.caption.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = colors.textSecondary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }

                        Text(
                            text = "· ${surah.totalVerses} Ayahs",
                            style = typography.caption.copy(fontSize = 11.sp),
                            color = colors.textSecondary
                        )
                    }
                }
            }

            // Right: Arabic Calligraphy Name (Amiri Quran)
            Text(
                text = surah.nameArabic,
                style = typography.arabicQuran.copy(
                    fontSize = 22.sp,
                    lineHeight = 28.sp
                ),
                color = colors.accent,
                textAlign = TextAlign.Right
            )
        }
    }
}
