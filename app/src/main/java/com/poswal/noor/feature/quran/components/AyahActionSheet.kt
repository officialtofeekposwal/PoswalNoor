package com.poswal.noor.feature.quran.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.data.local.entity.ContentItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyahActionSheet(
    item: ContentItemEntity,
    isBookmarked: Boolean,
    onDismiss: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCopyText: () -> Unit,
    onShareText: () -> Unit,
    onSaveNote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    var showNoteDialog by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surfaceElevated,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NoorSpacing.l)
                .padding(bottom = 36.dp)
        ) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = item.reference,
                        style = typography.subtitle.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Ayah Actions",
                        style = typography.caption,
                        color = colors.textSecondary
                    )
                }

                Surface(
                    color = colors.primaryContainer,
                    shape = NoorShapes.chip
                ) {
                    Text(
                        text = "Surah ${item.surah ?: 1}",
                        style = typography.caption.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = colors.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(NoorSpacing.l))

            if (!showNoteDialog) {
                // Action Items List
                ActionRow(
                    icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    title = if (isBookmarked) "Remove Bookmark" else "Bookmark Ayah",
                    subtitle = if (isBookmarked) "Saved in your collections" else "Save for quick reflection",
                    tint = if (isBookmarked) colors.accent else colors.textPrimary,
                    onClick = {
                        onToggleBookmark()
                        onDismiss()
                    }
                )

                ActionRow(
                    icon = Icons.Default.ContentCopy,
                    title = "Copy Ayah",
                    subtitle = "Copy Arabic scripture and active translation",
                    tint = colors.textPrimary,
                    onClick = {
                        onCopyText()
                        onDismiss()
                    }
                )

                ActionRow(
                    icon = Icons.Default.Share,
                    title = "Share Ayah",
                    subtitle = "Share citation and reflection with others",
                    tint = colors.textPrimary,
                    onClick = {
                        onShareText()
                        onDismiss()
                    }
                )

                ActionRow(
                    icon = Icons.Default.EditNote,
                    title = "Add Reflection Note",
                    subtitle = "Attach personal thoughts or lessons",
                    tint = colors.textPrimary,
                    onClick = {
                        showNoteDialog = true
                    }
                )
            } else {
                // Note editing inline state
                Text(
                    text = "Personal Reflection",
                    style = typography.subtitle.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(NoorSpacing.s))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text("Write your reflection here...", style = typography.body) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = NoorShapes.card,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(NoorSpacing.m))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { showNoteDialog = false },
                        shape = NoorShapes.chip
                    ) {
                        Text("Back", color = colors.textSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            onSaveNote(noteText)
                            onDismiss()
                        },
                        shape = NoorShapes.chip
                    ) {
                        Text("Save Note", color = colors.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(NoorShapes.chip)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(NoorSpacing.m))

        Column {
            Text(
                text = title,
                style = typography.body.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
            Text(
                text = subtitle,
                style = typography.caption.copy(fontSize = 12.sp),
                color = colors.textSecondary
            )
        }
    }
}
