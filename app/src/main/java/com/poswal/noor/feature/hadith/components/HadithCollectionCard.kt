package com.poswal.noor.feature.hadith.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
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
import com.poswal.noor.feature.hadith.model.HadithCollectionInfo

@Composable
fun HadithCollectionCard(
    collection: HadithCollectionInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    CalmCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NoorSpacing.l)
        ) {
            // Header Row: Badges & Calligraphy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = colors.primaryContainer,
                    shape = NoorShapes.chip
                ) {
                    Text(
                        text = collection.grading.uppercase(),
                        style = typography.caption.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = colors.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = collection.titleArabic,
                    style = typography.arabicQuran.copy(
                        fontSize = 22.sp,
                        lineHeight = 26.sp
                    ),
                    color = colors.accent,
                    textAlign = TextAlign.Right
                )
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // English Title
            Text(
                text = collection.titleEnglish,
                style = typography.title.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Author
            Text(
                text = collection.author,
                style = typography.caption.copy(fontSize = 12.sp),
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(NoorSpacing.s))

            // Description
            Text(
                text = collection.description,
                style = typography.body.copy(fontSize = 13.sp, lineHeight = 18.sp),
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(NoorSpacing.l))

            // Footer: Stats & Explore button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${collection.totalBooks} Books · ${collection.totalHadiths} Hadiths",
                    style = typography.caption.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colors.textPrimary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Browse Books",
                        style = typography.caption.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
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
    }
}
