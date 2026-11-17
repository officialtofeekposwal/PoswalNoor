package com.poswal.noor.feature.quran.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorMotion
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmCard
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.isFallbackTranslation
import com.poswal.noor.domain.model.translationFor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AyahItemCard(
    item: ContentItemEntity,
    language: AppLanguage,
    isBookmarked: Boolean,
    arabicFontSizeSp: Float,
    translationFontSizeSp: Float,
    showTranslation: Boolean,
    lineSpacingMultiplier: Float,
    onAyahClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val reduceMotion = NoorThemeTokens.reduceMotion

    val translationText = item.translationFor(language) ?: item.translationEn
    val isFallback = item.isFallbackTranslation(language)

    val bookmarkScale by animateFloatAsState(
        targetValue = if (isBookmarked && !reduceMotion) 1.2f else 1.0f,
        animationSpec = NoorMotion.springDefault(),
        label = "ayahBookmarkScale"
    )

    CalmCard(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onAyahClick,
                onLongClick = onAyahClick
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NoorSpacing.l)
        ) {
            // Header: Ayah Number Badge + Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ayah Number Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (item.ayah ?: 1).toString(),
                            style = typography.caption.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = colors.textPrimary
                        )
                    }

                    if (isFallback && showTranslation) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = colors.accent.copy(alpha = 0.15f),
                            shape = NoorShapes.chip
                        ) {
                            Text(
                                text = "EN",
                                style = typography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = colors.accent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Actions: Bookmark indicator + More menu trigger
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isBookmarked) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Bookmarked",
                            tint = colors.accent,
                            modifier = Modifier
                                .size(18.dp)
                                .graphicsLayer {
                                    scaleX = bookmarkScale
                                    scaleY = bookmarkScale
                                }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    IconButton(
                        onClick = onAyahClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Ayah Actions",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // Arabic Quran Scripture (RTL, Amiri font, adjustable size & line height)
            val arabicLineHeight = (arabicFontSizeSp * lineSpacingMultiplier).sp
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = item.arabic,
                    style = typography.arabicQuran.copy(
                        fontSize = arabicFontSizeSp.sp,
                        lineHeight = arabicLineHeight,
                        textAlign = TextAlign.Right
                    ),
                    color = colors.textPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Translation Block (if enabled)
            if (showTranslation && !translationText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(NoorSpacing.m))

                val isRtlLang = language.isRtl && !isFallback
                CompositionLocalProvider(
                    LocalLayoutDirection provides (if (isRtlLang) LayoutDirection.Rtl else LayoutDirection.Ltr)
                ) {
                    Text(
                        text = translationText,
                        style = if (language == AppLanguage.URDU && !isFallback) {
                            typography.urduNastaliq.copy(
                                fontSize = (translationFontSizeSp + 1).sp,
                                lineHeight = (translationFontSizeSp * 2.0f).sp,
                                textAlign = TextAlign.Right
                            )
                        } else {
                            typography.body.copy(
                                fontSize = translationFontSizeSp.sp,
                                lineHeight = (translationFontSizeSp * 1.5f).sp,
                                textAlign = if (isRtlLang) TextAlign.Right else TextAlign.Left
                            )
                        },
                        color = colors.textPrimary.copy(alpha = 0.88f),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
