package com.poswal.noor.ui.home

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.poswal.noor.R
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.model.Topic
import com.poswal.noor.domain.model.isFallbackTranslation
import com.poswal.noor.domain.model.translationFor
import com.poswal.noor.ui.components.glass.LiquidGlassCard
import com.poswal.noor.ui.components.glass.LiquidGlassDefaults
import com.poswal.noor.ui.theme.ArabicQuranTextStyle
import com.poswal.noor.ui.theme.HeadingFontFamily
import com.poswal.noor.ui.theme.HindiTranslationTextStyle
import com.poswal.noor.ui.theme.NoorEmerald
import com.poswal.noor.ui.theme.NoorEmeraldDeep
import com.poswal.noor.ui.theme.NoorGold
import com.poswal.noor.ui.theme.NoorIvory
import com.poswal.noor.ui.theme.NoorMotion
import com.poswal.noor.ui.theme.UrduTranslationTextStyle
import com.poswal.noor.util.WidgetPinHelper
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToQuran: () -> Unit = {},
    onNavigateToHadith: () -> Unit = {},
    onNavigateToDua: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToBookmarks: () -> Unit = {},
    onNavigateToTasbih: () -> Unit = {},
    onNavigateToQibla: () -> Unit = {},
    onNavigateToPrayerTimes: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagedItems = viewModel.pagedFeed.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // 🌟 1. TOP HEADER & BRAND BAR
            // ==========================================
            item(key = "header_section") {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Brand Avatar + Title
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
                                    text = "Daily Quran & Hadith Feed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Right Actions: Search, Shuffle/Refresh, Widget Pin, Settings
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(onClick = onNavigateToSearch) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = NoorEmerald
                                )
                            }

                            IconButton(onClick = { viewModel.refreshFeed() }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Shuffle Feed",
                                    tint = NoorEmerald
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(NoorEmerald.copy(alpha = 0.12f))
                                    .clickable { WidgetPinHelper.pinWidget(context) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Widgets,
                                        contentDescription = null,
                                        tint = NoorEmerald,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Widget",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = NoorEmerald
                                    )
                                }
                            }

                            IconButton(onClick = onNavigateToSettings) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Feature Navigation Hub
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FeatureHubPill(
                            icon = Icons.Default.Book,
                            label = "Quran (114)",
                            onClick = onNavigateToQuran
                        )
                        FeatureHubPill(
                            icon = Icons.Default.Bookmark,
                            label = "Hadith (14k)",
                            onClick = onNavigateToHadith
                        )
                        FeatureHubPill(
                            icon = Icons.Default.Share,
                            label = "Duas",
                            onClick = onNavigateToDua
                        )
                        FeatureHubPill(
                            icon = Icons.Default.Refresh,
                            label = "Tasbih",
                            onClick = onNavigateToTasbih
                        )
                        FeatureHubPill(
                            icon = Icons.Default.Widgets,
                            label = "Prayer & Qibla",
                            onClick = onNavigateToPrayerTimes
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ==========================================
                    // 🔘 2. SEGMENTED SLIDING PILL SWITCH (Both / Quran / Hadith)
                    // ==========================================
                    val segments = listOf(
                        ContentType.BOTH to stringResource(id = R.string.label_both),
                        ContentType.QURAN to stringResource(id = R.string.label_quran),
                        ContentType.HADITH to stringResource(id = R.string.label_hadith)
                    )
                    val selectedIndex = when (uiState.selectedContentType) {
                        ContentType.BOTH -> 0
                        ContentType.QURAN -> 1
                        ContentType.HADITH -> 2
                    }

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(3.dp)
                    ) {
                        val segmentWidth = maxWidth / 3
                        val pillOffset by androidx.compose.animation.core.animateDpAsState(
                            targetValue = segmentWidth * selectedIndex,
                            animationSpec = androidx.compose.animation.core.spring(
                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                            ),
                            label = "pillOffset"
                        )

                        Box(
                            modifier = Modifier
                                .width(segmentWidth)
                                .fillMaxHeight()
                                .offset(x = pillOffset)
                                .shadow(3.dp, RoundedCornerShape(11.dp), spotColor = NoorEmerald.copy(alpha = 0.35f))
                                .clip(RoundedCornerShape(11.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(NoorEmerald, NoorEmeraldDeep)
                                    )
                                )
                        )

                        Row(modifier = Modifier.fillMaxSize()) {
                            segments.forEachIndexed { index, (type, label) ->
                                val isSelected = selectedIndex == index
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(11.dp))
                                        .clickable { viewModel.selectContentType(type) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) NoorIvory else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ==========================================
                    // 🏷️ 3. TOPIC FILTER CHIPS (Horizontal Scroll)
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
                }
            }

            // ==========================================
            // 🔄 4. INITIAL LOADING SKELETON
            // ==========================================
            if (pagedItems.loadState.refresh is LoadState.Loading) {
                items(3) {
                    FeedItemSkeleton()
                }
            }

            // ==========================================
            // 📖 5. INFINITE FEED ITEMS (PAGING 3)
            // ==========================================
            items(
                count = pagedItems.itemCount,
                key = pagedItems.itemKey { it.id },
                contentType = pagedItems.itemContentType { it.type }
            ) { index ->
                val item = pagedItems[index]
                if (item != null) {
                    FeedCard(
                        item = item,
                        language = uiState.language,
                        onItemVisible = { viewModel.onItemVisible(it) },
                        onNavigateToDetail = onNavigateToDetail,
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        isBookmarkedFlow = viewModel.isBookmarked(item.id)
                    )
                }
            }

            // ==========================================
            // ⏳ 6. APPEND LOADING / EMPTY STATE
            // ==========================================
            if (pagedItems.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NoorEmerald, modifier = Modifier.size(28.dp))
                    }
                }
            }

            if (pagedItems.loadState.refresh is LoadState.NotLoading && pagedItems.itemCount == 0) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No reflections found for this filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeedCard(
    item: ContentItemEntity,
    language: AppLanguage,
    onItemVisible: (ContentItemEntity) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onToggleFavorite: (ContentItemEntity) -> Unit,
    isBookmarkedFlow: kotlinx.coroutines.flow.Flow<Boolean>
) {
    val context = LocalContext.current
    val isBookmarked by isBookmarkedFlow.collectAsState(initial = false)

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }
    val alphaAnim by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = NoorMotion.DurationMedium,
            easing = NoorMotion.StandardEasing
        ),
        label = "feedItemAlpha"
    )
    val translationYAnim by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isVisible) 0f else 20f,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = NoorMotion.DurationMedium,
            easing = NoorMotion.StandardEasing
        ),
        label = "feedItemY"
    )

    // Only mark item as seen if user viewed it for at least 1 second (per requirement)
    LaunchedEffect(item.id) {
        delay(1000)
        onItemVisible(item)
    }

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = alphaAnim
                translationY = translationYAnim
            },
        contentPadding = 20.dp,
        onClick = { onNavigateToDetail(item.id) }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Row: Type Tag, Reference & Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.type.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = NoorEmerald,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NoorEmerald.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )

                    if (!item.grade.isNullOrEmpty()) {
                        Text(
                            text = item.grade,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = NoorGold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NoorGold.copy(alpha = 0.15f))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onToggleFavorite(item) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) NoorGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }

                    IconButton(
                        onClick = {
                            try {
                                com.poswal.noor.ui.share.ImageCardGenerator.shareImageCard(context, item, language)
                            } catch (e: Exception) {
                                val shareText = "${item.arabic}\n\n${item.translationFor(language) ?: item.translationEn}\n\n— ${item.reference}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Reflection"))
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Calligraphy Text (Offline Amiri)
            Text(
                text = item.arabic,
                style = ArabicQuranTextStyle,
                fontSize = 22.sp,
                lineHeight = 40.sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Translation Text (Localized font)
            val translation = item.translationFor(language)
            if (translation != null) {
                val translationStyle = when (language) {
                    AppLanguage.URDU -> UrduTranslationTextStyle
                    AppLanguage.HINDI -> HindiTranslationTextStyle
                    else -> MaterialTheme.typography.bodyLarge
                }

                Text(
                    text = translation,
                    style = translationStyle,
                    fontSize = 15.sp,
                    lineHeight = if (language == AppLanguage.URDU) 30.sp else 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Fallback Indicator Tag
            if (item.isFallbackTranslation(language)) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Showing English translation",
                    style = MaterialTheme.typography.labelSmall,
                    color = NoorGold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(NoorGold.copy(alpha = 0.10f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Citation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.reference,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NoorEmerald
                )

                Text(
                    text = "Read more ›",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = NoorGold
                )
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
        if (isSelected) NoorEmerald else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
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
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

@Composable
fun FeedItemSkeleton() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha)
        )
    ) {}
}

@Composable
fun FeatureHubPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(NoorEmerald.copy(alpha = 0.12f))
            .border(1.dp, NoorEmerald.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NoorEmerald,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = NoorEmeraldDeep
            )
        }
    }
}
