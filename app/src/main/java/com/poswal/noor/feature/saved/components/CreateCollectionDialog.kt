package com.poswal.noor.feature.saved.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens

@Composable
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    var collectionName by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Collection",
                style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Create a custom folder to organize your saved Ayahs and Hadiths (e.g., 'Morning Duas', 'Sabr', 'Reflection').",
                    style = typography.body.copy(fontSize = 13.sp),
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(NoorSpacing.m))

                OutlinedTextField(
                    value = collectionName,
                    onValueChange = {
                        collectionName = it
                        if (it.isNotBlank()) errorText = null
                    },
                    label = { Text("Collection Name") },
                    placeholder = { Text("e.g. Daily Inspiration") },
                    singleLine = true,
                    isError = errorText != null,
                    supportingText = errorText?.let { { Text(it, color = colors.error) } },
                    shape = NoorShapes.chip,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedIndicatorColor = colors.primary,
                        unfocusedIndicatorColor = colors.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmed = collectionName.trim()
                    if (trimmed.isBlank()) {
                        errorText = "Name cannot be empty"
                    } else {
                        onConfirm(trimmed)
                        onDismiss()
                    }
                }
            ) {
                Text("Create", color = colors.primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.textSecondary)
            }
        },
        containerColor = colors.surface,
        shape = NoorShapes.card
    )
}
