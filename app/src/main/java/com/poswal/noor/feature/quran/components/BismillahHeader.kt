package com.poswal.noor.feature.quran.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun BismillahHeader(
    surahNumber: Int,
    modifier: Modifier = Modifier
) {
    // Exclude Surah 1 (Al-Fatihah, whose 1st ayah is Bismillah) and Surah 9 (At-Tawbah)
    if (surahNumber == 1 || surahNumber == 9) return

    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(NoorShapes.card)
            .background(colors.surfaceElevated.copy(alpha = 0.6f))
            .padding(vertical = NoorSpacing.l, horizontal = NoorSpacing.m),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                style = typography.arabicQuran.copy(
                    fontSize = 24.sp,
                    lineHeight = 44.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "In the name of Allah, the Entirely Merciful, the Especially Merciful",
                style = typography.caption.copy(fontSize = 11.sp),
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
