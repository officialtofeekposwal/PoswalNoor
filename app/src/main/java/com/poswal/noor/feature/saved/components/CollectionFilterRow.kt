package com.poswal.noor.feature.saved.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionFilterRow(
    collections: List<String>,
    selectedCollection: String?,
    onSelectCollection: (String?) -> Unit,
    onNewCollectionClick: () -> Unit,
    onDeleteCollection: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = NoorSpacing.l, vertical = NoorSpacing.xs),
        horizontalArrangement = Arrangement.spacedBy(NoorSpacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "All Collections" Chip
        val isAllSelected = selectedCollection == null
        val allBgColor by animateColorAsState(
            targetValue = if (isAllSelected) colors.primary else colors.surface,
            label = "all_chip_bg"
        )
        val allTextColor by animateColorAsState(
            targetValue = if (isAllSelected) colors.onPrimary else colors.textPrimary,
            label = "all_chip_text"
        )

        Box(
            modifier = Modifier
                .height(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(allBgColor)
                .border(
                    width = 1.dp,
                    color = if (isAllSelected) colors.primary else colors.outline,
                    shape = RoundedCornerShape(12.dp)
                )
                .combinedClickable(
                    onClick = { onSelectCollection(null) }
                )
                .padding(horizontal = NoorSpacing.m),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "All Collections",
                style = typography.caption.copy(
                    fontSize = 12.sp,
                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = allTextColor
            )
        }

        // Custom collections
        collections.forEach { collectionName ->
            val isSelected = selectedCollection == collectionName
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) colors.primary else colors.surface,
                label = "chip_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) colors.onPrimary else colors.textPrimary,
                label = "chip_text"
            )

            Box(
                modifier = Modifier
                    .height(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) colors.primary else colors.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .combinedClickable(
                        onClick = { onSelectCollection(collectionName) },
                        onLongClick = {
                            if (collectionName != "Default") {
                                onDeleteCollection(collectionName)
                            }
                        }
                    )
                    .padding(horizontal = NoorSpacing.m),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.FolderOpen else Icons.Default.Folder,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(NoorSpacing.xs))
                    Text(
                        text = collectionName,
                        style = typography.caption.copy(
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = textColor
                    )
                }
            }
        }

        // "+ New Collection" Button Chip
        Box(
            modifier = Modifier
                .height(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.primaryContainer.copy(alpha = 0.5f))
                .border(
                    width = 1.dp,
                    color = colors.primary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .combinedClickable(onClick = onNewCollectionClick)
                .padding(horizontal = NoorSpacing.m),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Collection",
                    tint = colors.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(NoorSpacing.xs))
                Text(
                    text = "New",
                    style = typography.caption.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colors.primary
                )
            }
        }
    }
}
