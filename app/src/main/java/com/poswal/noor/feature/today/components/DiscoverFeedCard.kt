package com.poswal.noor.feature.today.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.poswal.noor.data.local.entity.isQuran
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.isFallbackTranslation
import com.poswal.noor.domain.model.translationFor
import kotlinx.coroutines.delay

@Composable
fun DiscoverFeedCard(
    item: ContentItemEntity,
    language: AppLanguage,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onShareClick: () -> Unit,
    onClick: () -> Unit,
    onItemVisible: (ContentItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val reduceMotion = NoorThemeTokens.reduceMotion

    // 1-second view dwell detection for No-Repeat engine
    LaunchedEffect(item.id) {
        delay(1000L)
        onItemVisible(item)
    }

    val bookmarkScale by animateFloatAsState(
        targetValue = if (isBookmarked && !reduceMotion) 1.25f else 1.0f,
        animationSpec = NoorMotion.springDefault(),
        label = "feedBookmarkScale"
    )

    val translationText = item.translationFor(language) ?: item.translationEn
    val isFallback = item.isFallbackTranslation(language)

    CalmCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NoorSpacing.l)
        ) {
            // Header Row: Type Badge + Topic Tag + Fallback Indicator + Bookmark/Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Type Badge (Quran / Hadith)
                    Surface(
                        color = if (item.isQuran) colors.primaryContainer else colors.surfaceElevated,
                        shape = NoorShapes.chip
                    ) {
                        Text(
                            text = if (item.isQuran) "Quran" else "Hadith",
                            style = typography.label.copy(fontWeight = FontWeight.SemiBold),
                            color = if (item.isQuran) colors.primary else colors.textSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Topic Tag (if present)
                    if (!item.topic.isNullOrBlank()) {
                        Surface(
                            color = colors.surfaceElevated,
                            shape = NoorShapes.chip
                        ) {
                            Text(
                                text = item.topic.replaceFirstChar { it.uppercase() },
                                style = typography.caption.copy(fontSize = 11.sp),
                                color = colors.textSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Fallback Badge (if active language translation is unavailable for this row)
                    if (isFallback) {
                        Surface(
                            color = colors.accent.copy(alpha = 0.15f),
                            shape = NoorShapes.chip
                        ) {
                            Text(
                                text = "EN fallback",
                                style = typography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                                color = colors.accent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Actions: Bookmark & Share
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Saved" else "Save",
                            tint = if (isBookmarked) colors.accent else colors.textSecondary,
                            modifier = Modifier
                                .size(20.dp)
                                .graphicsLayer {
                                    scaleX = bookmarkScale
                                    scaleY = bookmarkScale
                                }
                        )
                    }

                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // Arabic Scripture Block (RTL)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = item.arabic,
                    style = typography.arabicQuran.copy(
                        fontSize = if (item.isQuran) 24.sp else 22.sp,
                        lineHeight = if (item.isQuran) 48.sp else 44.sp
                    ),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // Translation Text (LTR or RTL depending on target language)
            val isRtlLang = language.isRtl && !isFallback
            CompositionLocalProvider(
                LocalLayoutDirection provides (if (isRtlLang) LayoutDirection.Rtl else LayoutDirection.Ltr)
            ) {
                Text(
                    text = translationText ?: "",
                    style = if (language == AppLanguage.URDU && !isFallback) typography.urduNastaliq.copy(lineHeight = 36.sp)
                    else typography.body.copy(lineHeight = 24.sp),
                    color = colors.textPrimary.copy(alpha = 0.9f),
                    textAlign = if (isRtlLang) TextAlign.Right else TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(NoorSpacing.l))

            // Citation / Reference Footer
            val referenceText = item.reference

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = referenceText,
                    style = typography.caption.copy(fontWeight = FontWeight.Medium),
                    color = colors.textSecondary
                )

                // Subtle arrow indicator to view full detail
                Text(
                    text = "Read more →",
                    style = typography.caption.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primary
                    )
                )
            }
        }
    }
}
