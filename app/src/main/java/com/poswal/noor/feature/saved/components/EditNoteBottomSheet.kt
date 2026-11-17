package com.poswal.noor.feature.saved.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.domain.model.SavedItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteBottomSheet(
    savedItem: SavedItem,
    onDismiss: () -> Unit,
    onSaveNote: (String?) -> Unit
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var noteText by remember { mutableStateOf(savedItem.bookmark.note ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        shape = NoorShapes.sheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NoorSpacing.l)
                .padding(bottom = NoorSpacing.xxl)
        ) {
            Text(
                text = "Personal Reflection",
                style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(NoorSpacing.xs))

            Text(
                text = "${savedItem.content.type.uppercase()} · ${savedItem.content.reference}",
                style = typography.caption.copy(fontSize = 12.sp, color = colors.primary, fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Write your thoughts or reminders...") },
                placeholder = { Text("What did you learn from this verse? How can you apply it today?") },
                minLines = 4,
                maxLines = 8,
                shape = NoorShapes.chip,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.surfaceElevated,
                    unfocusedContainerColor = colors.surfaceElevated,
                    focusedIndicatorColor = colors.primary,
                    unfocusedIndicatorColor = colors.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(NoorSpacing.l))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!savedItem.bookmark.note.isNullOrBlank()) {
                    OutlinedButton(
                        onClick = {
                            onSaveNote(null)
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.error),
                        shape = NoorShapes.chip
                    ) {
                        Text("Delete Note")
                    }

                    Spacer(modifier = Modifier.width(NoorSpacing.m))
                }

                Button(
                    onClick = {
                        val trimmed = noteText.trim()
                        onSaveNote(trimmed.ifBlank { null })
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    ),
                    shape = NoorShapes.chip
                ) {
                    Text("Save Reflection", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
