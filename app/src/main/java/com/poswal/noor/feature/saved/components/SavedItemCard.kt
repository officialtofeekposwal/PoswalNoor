package com.poswal.noor.feature.saved.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmCard
import com.poswal.noor.domain.model.SavedItem

@Composable
fun SavedItemCard(
    savedItem: SavedItem,
    onItemClick: () -> Unit,
    onMoveCollectionClick: () -> Unit,
    onEditNoteClick: () -> Unit,
    onShareClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val content = savedItem.content
    val isQuran = content.type.equals("quran", ignoreCase = true)

    CalmCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onItemClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NoorSpacing.l)
        ) {
            // Header Row: Type pill, Reference & Collection folder tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(NoorSpacing.s)
                ) {
                    // Type Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isQuran) colors.primaryContainer else colors.accent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isQuran) "QURAN" else "HADITH",
                            style = typography.caption.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = if (isQuran) colors.primary else colors.accent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Reference
                    Text(
                        text = content.reference,
                        style = typography.caption.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = colors.textPrimary
                    )
                }

                // Folder Chip (if not Default)
                if (savedItem.bookmark.folder != "Default") {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colors.surfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = colors.accent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = savedItem.bookmark.folder,
                                style = typography.caption.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // Arabic Scripture
            Text(
                text = content.arabic,
                style = typography.arabicQuran.copy(
                    fontSize = 22.sp,
                    lineHeight = 42.sp
                ),
                color = colors.textPrimary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(NoorSpacing.s))

            // Translation (English default)
            Text(
                text = content.translationEn,
                style = typography.body.copy(
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                ),
                color = colors.textSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            // Personal Reflection Note (if present)
            AnimatedVisibility(
                visible = !savedItem.bookmark.note.isNullOrBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                savedItem.bookmark.note?.let { note ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = NoorSpacing.m)
                            .clip(NoorShapes.chip)
                            .background(colors.surfaceElevated)
                            .border(
                                width = 1.dp,
                                color = colors.accent.copy(alpha = 0.3f),
                                shape = NoorShapes.chip
                            )
                            .clickable { onEditNoteClick() }
                            .padding(NoorSpacing.m)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = colors.accent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(NoorSpacing.xs))
                                Text(
                                    text = "Reflection Note",
                                    style = typography.caption.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = colors.accent
                                )
                            }
                            Text(
                                text = "Edit",
                                style = typography.caption.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.primary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(NoorSpacing.xs))

                        Text(
                            text = note,
                            style = typography.body.copy(
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            color = colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // Action Row: Move, Note, Share, Remove
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Move Collection
                IconButton(
                    onClick = onMoveCollectionClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.DriveFileMove,
                        contentDescription = "Move Collection",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Add / Edit Note
                IconButton(
                    onClick = onEditNoteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Note",
                        tint = if (savedItem.bookmark.note != null) colors.accent else colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Share
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

                // Remove Bookmark
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkRemove,
                        contentDescription = "Remove Bookmark",
                        tint = colors.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
