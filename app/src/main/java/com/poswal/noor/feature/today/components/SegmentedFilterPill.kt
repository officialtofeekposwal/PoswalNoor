package com.poswal.noor.feature.today.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorMotion
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.domain.model.ContentType

@Composable
fun SegmentedFilterPill(
    selectedType: ContentType,
    onTypeSelected: (ContentType) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val reduceMotion = NoorThemeTokens.reduceMotion

    val segments = listOf(
        ContentType.BOTH to "Both",
        ContentType.QURAN to "Quran",
        ContentType.HADITH to "Hadith"
    )

    val selectedIndex = when (selectedType) {
        ContentType.BOTH -> 0
        ContentType.QURAN -> 1
        ContentType.HADITH -> 2
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(NoorShapes.pill)
            .background(colors.surfaceElevated)
            .border(1.dp, colors.outline.copy(alpha = 0.5f), NoorShapes.pill)
            .padding(3.dp)
    ) {
        val segmentWidth = maxWidth / 3

        val pillOffset by animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = if (reduceMotion) NoorMotion.standardTween(0) else NoorMotion.springDefault(),
            label = "segmentedPillOffset"
        )

        // Sliding Active Pill
        Box(
            modifier = Modifier
                .width(segmentWidth)
                .fillMaxHeight()
                .offset(x = pillOffset)
                .clip(NoorShapes.pill)
                .background(colors.primary)
        )

        // Segment Labels
        Row(modifier = Modifier.fillMaxSize()) {
            segments.forEachIndexed { index, (type, label) ->
                val isSelected = selectedIndex == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(NoorShapes.pill)
                        .clickable { onTypeSelected(type) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = typography.label.copy(
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) colors.onPrimary else colors.textSecondary
                    )
                }
            }
        }
    }
}
