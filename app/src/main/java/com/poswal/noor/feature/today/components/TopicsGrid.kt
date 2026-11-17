package com.poswal.noor.feature.today.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.poswal.noor.core.designsystem.components.CalmCard

data class TopicItem(
    val key: String,
    val title: String,
    val arabic: String,
    val icon: ImageVector
)

val NINE_TOPICS = listOf(
    TopicItem("namaz", "Prayer", "صلاة", Icons.Default.Mosque),
    TopicItem("sabr", "Patience", "صبر", Icons.Default.HourglassBottom),
    TopicItem("akhlaq", "Manners", "أخلاق", Icons.Default.Mood),
    TopicItem("parents", "Parents", "والدين", Icons.Default.Diversity3),
    TopicItem("dua", "Dua", "دعاء", Icons.Default.Favorite),
    TopicItem("akhirah", "Hereafter", "آخرة", Icons.Default.AutoAwesome),
    TopicItem("ilm", "Knowledge", "علم", Icons.AutoMirrored.Filled.MenuBook),
    TopicItem("charity", "Charity", "صدقة", Icons.Default.VolunteerActivism),
    TopicItem("family", "Family", "أسرة", Icons.Default.Diversity1)
)

@Composable
fun TopicsGrid(
    selectedTopicKey: String?,
    onTopicSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Themes & Topics",
                style = typography.subtitle.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )

            if (selectedTopicKey != null) {
                Row(
                    modifier = Modifier
                        .clip(NoorShapes.pill)
                        .background(colors.primaryContainer)
                        .clickable { onTopicSelected(null) }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Clear filter",
                        style = typography.label.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                        color = colors.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(NoorSpacing.m))

        // 3x3 Grid
        Column(verticalArrangement = Arrangement.spacedBy(NoorSpacing.s)) {
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(NoorSpacing.s)
                ) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        if (index < NINE_TOPICS.size) {
                            val topic = NINE_TOPICS[index]
                            val isSelected = selectedTopicKey == topic.key

                            Box(modifier = Modifier.weight(1f)) {
                                CalmCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = NoorShapes.chip,
                                    backgroundColor = if (isSelected) colors.primaryContainer else colors.surface,
                                    borderColor = if (isSelected) colors.primary else colors.outline,
                                    onClick = {
                                        if (isSelected) onTopicSelected(null)
                                        else onTopicSelected(topic.key)
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = topic.icon,
                                            contentDescription = topic.title,
                                            tint = if (isSelected) colors.primary else colors.textSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = topic.title,
                                            style = typography.label.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                            color = if (isSelected) colors.primary else colors.textPrimary,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = topic.arabic,
                                            style = typography.arabicQuran.copy(fontSize = 10.sp, lineHeight = 12.sp),
                                            color = colors.accent,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
