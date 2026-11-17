package com.poswal.noor.feature.hadith.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorMotion
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmCard
import com.poswal.noor.data.local.entity.ContentItemEntity

@Composable
fun HadithFeedCard(
    item: ContentItemEntity,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onShareClick: () -> Unit,
    onActionMenuClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val reduceMotion = NoorThemeTokens.reduceMotion

    val isLongText = (item.translationEn.length > 220 || item.wordCountEn > 40)
    var isExpanded by remember { mutableStateOf(false) }

    val bookmarkScale by animateFloatAsState(
        targetValue = if (isBookmarked && !reduceMotion) 1.25f else 1.0f,
        animationSpec = NoorMotion.springDefault(),
        label = "hadithBookmarkScale"
    )

    CalmCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NoorSpacing.l)
                .animateContentSize(animationSpec = NoorMotion.springDefault())
        ) {
            // Header Row: Reference + Grading Badge + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Grading Badge (authentic dataset grade only)
                    if (!item.grade.isNullOrBlank()) {
                        Surface(
                            color = colors.primaryContainer,
                            shape = NoorShapes.chip
                        ) {
                            Text(
                                text = item.grade.uppercase(),
                                style = typography.caption.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = colors.primary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Topic Chip (if present)
                    if (!item.topic.isNullOrBlank()) {
                        Surface(
                            color = colors.surfaceElevated,
                            shape = NoorShapes.chip
                        ) {
                            Text(
                                text = item.topic.replaceFirstChar { it.uppercase() },
                                style = typography.caption.copy(fontSize = 11.sp),
                                color = colors.textSecondary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Top Actions (Bookmark & More Menu)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Saved" else "Save",
                            tint = if (isBookmarked) colors.accent else colors.textSecondary,
                            modifier = Modifier
                                .size(18.dp)
                                .graphicsLayer {
                                    scaleX = bookmarkScale
                                    scaleY = bookmarkScale
                                }
                        )
                    }

                    IconButton(
                        onClick = onActionMenuClick,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Actions",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // Narrator Tag (if available)
            if (!item.narrator.isNullOrBlank()) {
                Text(
                    text = "Narrated by ${item.narrator}",
                    style = typography.caption.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Authentic Arabic Hadith Scripture (Amiri Quran font, RTL)
            if (!item.arabic.isBlank()) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = item.arabic,
                        style = typography.arabicQuran.copy(
                            fontSize = 21.sp,
                            lineHeight = 42.sp
                        ),
                        color = colors.textPrimary,
                        textAlign = TextAlign.Right,
                        maxLines = if (isExpanded || !isLongText) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(NoorSpacing.m))
            }

            // English Translation with Expand/Collapse Support
            Text(
                text = item.translationEn,
                style = typography.body.copy(lineHeight = 23.sp),
                color = colors.textPrimary.copy(alpha = 0.9f),
                maxLines = if (isExpanded || !isLongText) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            // "Read more" / "Show less" Expand Button
            if (isLongText) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isExpanded) "Show less ↑" else "Read more ↓",
                    style = typography.caption.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    ),
                    modifier = Modifier
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // Footer Citation & Quick Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.reference,
                    style = typography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                    color = colors.textSecondary
                )

                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
