package com.poswal.noor.feature.today.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorMotion
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.isFallbackTranslation
import com.poswal.noor.domain.model.translationFor

@Composable
fun TodayHeroVerseCard(
    item: ContentItemEntity,
    language: AppLanguage,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onShareClick: () -> Unit,
    onPinWidgetClick: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val reduceMotion = NoorThemeTokens.reduceMotion

    val gradientColors = if (colors.isDark) {
        listOf(Color(0xFF14392D), Color(0xFF0C221B))
    } else {
        listOf(Color(0xFF0F6B4F), Color(0xFF094E38))
    }

    val bookmarkScale by animateFloatAsState(
        targetValue = if (isBookmarked && !reduceMotion) 1.2f else 1.0f,
        animationSpec = NoorMotion.springDefault(),
        label = "heroBookmarkScale"
    )

    val translationText = item.translationFor(language) ?: item.translationEn
    val isFallback = item.isFallbackTranslation(language)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(NoorShapes.hero)
            .clickable(onClick = onCardClick),
        shape = NoorShapes.hero,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(gradientColors))
                .padding(NoorSpacing.xl)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Tag: VERSE OF THE DAY
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(NoorShapes.pill)
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✨ VERSE OF THE DAY",
                            style = typography.label.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                            color = colors.accent
                        )
                    }

                    if (isFallback) {
                        Box(
                            modifier = Modifier
                                .clip(NoorShapes.pill)
                                .background(Color.Black.copy(alpha = 0.25f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "English Fallback",
                                style = typography.label.copy(fontSize = 10.sp),
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(NoorSpacing.l))

                // Arabic Calligraphy (Amiri Quran - 28sp / 2.0x line-height)
                Text(
                    text = item.arabic,
                    style = typography.arabicQuran.copy(
                        fontSize = 26.sp,
                        lineHeight = 52.sp,
                        textAlign = TextAlign.Right
                    ),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(NoorSpacing.m))

                // Localized Translation
                val translationStyle = when (language) {
                    AppLanguage.URDU -> typography.urduNastaliq.copy(fontSize = 16.sp, lineHeight = 34.sp)
                    AppLanguage.HINDI -> typography.hindiDevanagari.copy(fontSize = 15.sp, lineHeight = 24.sp)
                    else -> typography.body.copy(fontSize = 15.sp, lineHeight = 22.sp)
                }

                Text(
                    text = translationText,
                    style = translationStyle,
                    color = Color.White.copy(alpha = 0.92f),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(NoorSpacing.l))

                // Bottom Row: Reference Badge & Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.reference,
                        style = typography.caption.copy(fontWeight = FontWeight.Medium),
                        color = colors.accent
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Bookmark Action
                        IconButton(
                            onClick = onToggleBookmark,
                            modifier = Modifier.size(NoorSpacing.minTouchTarget)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "Save Verse",
                                tint = if (isBookmarked) colors.accent else Color.White.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .size(22.dp)
                                    .graphicsLayer {
                                        scaleX = bookmarkScale
                                        scaleY = bookmarkScale
                                    }
                            )
                        }

                        // Share Action
                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier.size(NoorSpacing.minTouchTarget)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Pin to Home Widget
                        IconButton(
                            onClick = onPinWidgetClick,
                            modifier = Modifier.size(NoorSpacing.minTouchTarget)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Widgets,
                                contentDescription = "Add Widget",
                                tint = colors.accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
