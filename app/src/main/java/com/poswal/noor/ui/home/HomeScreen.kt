package com.poswal.noor.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.R
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.model.Topic
import com.poswal.noor.domain.model.isFallbackTranslation
import com.poswal.noor.domain.model.translationFor
import com.poswal.noor.ui.theme.ArabicQuranTextStyle
import com.poswal.noor.ui.theme.HeadingFontFamily
import com.poswal.noor.ui.theme.NoorEmerald
import com.poswal.noor.ui.theme.NoorEmeraldDeep
import com.poswal.noor.ui.theme.NoorGold
import com.poswal.noor.ui.theme.NoorIvory
import com.poswal.noor.ui.theme.UrduTranslationTextStyle
import com.poswal.noor.util.WidgetPinHelper

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            // ==========================================
            // 🌟 ELEGANT CUSTOM TOP HEADER BAR
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Brand Logo + Typography
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NoorEmerald)
                            .border(1.5.dp, NoorGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_noor_mark),
                            contentDescription = null,
                            tint = NoorIvory,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Poswal Noor",
                                fontFamily = HeadingFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "نور",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NoorGold
                            )
                        }
                        Text(
                            text = "Daily Quran & Hadith",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Right: 1-Tap Widget Pill + Settings Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Sleek Widget Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(NoorEmerald.copy(alpha = 0.09f))
                            .border(1.dp, NoorGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { WidgetPinHelper.pinWidget(context) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = null,
                            tint = NoorEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "+ Widget",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = NoorEmerald
                        )
                    }

                    // Settings Button
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.action_settings),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // 🎯 SLEEK SEGMENTED CONTROL (ALL / QURAN / HADITH)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val segments = listOf(
                    ContentType.BOTH to stringResource(id = R.string.label_both),
                    ContentType.QURAN to stringResource(id = R.string.label_quran),
                    ContentType.HADITH to stringResource(id = R.string.label_hadith)
                )

                segments.forEach { (type, label) ->
                    val isSelected = uiState.selectedContentType == type
                    val bgColor by animateColorAsState(
                        if (isSelected) NoorEmerald else Color.Transparent,
                        label = "segmentBg"
                    )
                    val textColor by animateColorAsState(
                        if (isSelected) NoorIvory else MaterialTheme.colorScheme.onSurfaceVariant,
                        label = "segmentText"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .clickable { viewModel.selectContentType(type) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 🏷️ TOPIC FILTER CHIPS (HORIZONTAL SCROLL)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TopicChip(
                    label = stringResource(id = R.string.label_all_topics),
                    isSelected = uiState.selectedTopicKey == null,
                    onClick = { viewModel.selectTopic(null) }
                )

                Topic.entries.forEach { topic ->
                    val topicResId = when (topic) {
                        Topic.NAMAZ -> R.string.topic_namaz
                        Topic.SABR -> R.string.topic_sabr
                        Topic.AKHLAQ -> R.string.topic_akhlaq
                        Topic.PARENTS -> R.string.topic_parents
                        Topic.DUA -> R.string.topic_dua
                        Topic.AKHIRAH -> R.string.topic_akhirah
                        Topic.ILM -> R.string.topic_ilm
                        Topic.CHARITY -> R.string.topic_charity
                        Topic.FAMILY -> R.string.topic_family
                    }
                    TopicChip(
                        label = stringResource(id = topicResId),
                        isSelected = uiState.selectedTopicKey == topic.key,
                        onClick = { viewModel.selectTopic(topic.key) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // 📖 MAIN DAILY REFLECTION CARD
            // ==========================================
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NoorEmerald)
                }
            } else {
                val item = uiState.currentItem
                if (item != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = NoorEmerald.copy(alpha = 0.15f))
                            .clip(RoundedCornerShape(20.dp))
                            .border(
                                width = 1.dp,
                                color = NoorGold.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp)
                        ) {
                            // Top Row: Badges & Favorite
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Type badge
                                    Text(
                                        text = item.type.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = NoorEmerald,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NoorEmerald.copy(alpha = 0.12f))
                                            .padding(horizontal = 9.dp, vertical = 4.dp)
                                    )

                                    // Grade badge
                                    if (!item.grade.isNullOrEmpty()) {
                                        Text(
                                            text = item.grade,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NoorGold,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(NoorGold.copy(alpha = 0.15f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.toggleFavorite(item) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (item.isFavorite) NoorGold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Arabic Quran / Hadith Text (Amiri font)
                            Text(
                                text = item.arabic,
                                style = ArabicQuranTextStyle,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp)
                            )

                            // Translation Text
                            val translation = item.translationFor(uiState.language)
                            if (!translation.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))

                                // Fallback badge if applicable
                                if (item.isFallbackTranslation(uiState.language)) {
                                    Text(
                                        text = stringResource(id = R.string.badge_english_fallback),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NoorEmerald,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(NoorEmerald.copy(alpha = 0.08f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                Text(
                                    text = translation,
                                    style = if (uiState.language == AppLanguage.URDU) UrduTranslationTextStyle else MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
                                    textAlign = if (uiState.language.isRtl) TextAlign.Right else TextAlign.Left,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Reference in Bold Gold/Emerald
                            Text(
                                text = item.reference,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = NoorEmerald,
                                modifier = Modifier.align(if (uiState.language.isRtl) Alignment.Start else Alignment.End)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Action Buttons (Refresh + Read more)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.refreshRandomItem() },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = stringResource(id = R.string.action_refresh))
                                }

                                Button(
                                    onClick = { onNavigateToDetail(item.id) },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NoorEmerald,
                                        contentColor = NoorIvory
                                    )
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.action_read_more),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // 📱 ADD WIDGET BANNER
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = NoorGold.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { WidgetPinHelper.pinWidget(context) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(NoorEmerald.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = null,
                            tint = NoorEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.action_add_widget_to_home),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(id = R.string.action_add_widget_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { WidgetPinHelper.pinWidget(context) },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NoorEmerald,
                            contentColor = NoorIvory
                        )
                    ) {
                        Text(
                            text = "+ Add",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopicChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        if (isSelected) NoorEmerald else MaterialTheme.colorScheme.surfaceVariant,
        label = "chipBg"
    )
    val textColor by animateColorAsState(
        if (isSelected) NoorIvory else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "chipText"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(
                1.dp,
                if (isSelected) NoorGold.copy(alpha = 0.6f) else Color.Transparent,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}
