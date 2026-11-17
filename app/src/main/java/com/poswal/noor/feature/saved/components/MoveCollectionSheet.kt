package com.poswal.noor.feature.saved.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
fun MoveCollectionSheet(
    savedItem: SavedItem,
    availableCollections: List<String>,
    onDismiss: () -> Unit,
    onSelectCollection: (String) -> Unit,
    onCreateNewCollection: () -> Unit
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val allFolders = listOf("Default") + availableCollections.filter { it != "Default" }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        shape = NoorShapes.sheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = NoorSpacing.xxl)
        ) {
            Column(modifier = Modifier.padding(horizontal = NoorSpacing.l)) {
                Text(
                    text = "Move to Collection",
                    style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(NoorSpacing.xs))

                Text(
                    text = "Select a folder for ${savedItem.content.reference}",
                    style = typography.caption.copy(fontSize = 12.sp),
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))
            HorizontalDivider(color = colors.outline.copy(alpha = 0.5f))

            LazyColumn {
                items(allFolders) { folderName ->
                    val isCurrent = savedItem.bookmark.folder == folderName

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectCollection(folderName)
                                onDismiss()
                            }
                            .padding(horizontal = NoorSpacing.l, vertical = NoorSpacing.m),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = if (isCurrent) colors.primary else colors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(NoorSpacing.m))
                            Text(
                                text = folderName,
                                style = typography.body.copy(
                                    fontSize = 15.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isCurrent) colors.primary else colors.textPrimary
                            )
                        }

                        if (isCurrent) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                item {
                    HorizontalDivider(color = colors.outline.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onDismiss()
                                onCreateNewCollection()
                            }
                            .padding(horizontal = NoorSpacing.l, vertical = NoorSpacing.m),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreateNewFolder,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(NoorSpacing.m))
                        Text(
                            text = "Create New Collection...",
                            style = typography.body.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }
}
