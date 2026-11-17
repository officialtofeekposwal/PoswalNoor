package com.poswal.noor.feature.today

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmShimmerCard
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.local.entity.isQuran
import com.poswal.noor.domain.model.translationFor
import com.poswal.noor.feature.today.components.ContinueReadingCard
import com.poswal.noor.feature.today.components.DiscoverFeedCard
import com.poswal.noor.feature.today.components.SegmentedFilterPill
import com.poswal.noor.feature.today.components.TodayHeader
import com.poswal.noor.feature.today.components.TodayHeroVerseCard
import com.poswal.noor.feature.today.components.TopicsGrid
import com.poswal.noor.util.WidgetPinHelper

@Composable
fun TodayScreen(
    onNavigateToSearch: () -> Unit = {},
    onNavigateToVerseDetail: (Long) -> Unit = {},
    onNavigateToSurahReader: (surahNumber: Int, ayahNumber: Int) -> Unit = { _, _ -> },
    onNavigateToHadithDetail: (Long) -> Unit = {},
    viewModel: TodayViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    val uiState by viewModel.uiState.collectAsState()
    val pagedFeed = viewModel.pagedFeed.collectAsLazyPagingItems()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 100.dp // Clear space for the floating bottom navigation bar
            ),
            verticalArrangement = Arrangement.spacedBy(NoorSpacing.l)
        ) {
            // 1. Header (Greeting, Hijri date, Search, Widget Pin, Refresh)
            item(key = "today_header") {
                TodayHeader(
                    hijriDateFormatted = uiState.hijriDateFormatted,
                    onSearchClick = onNavigateToSearch,
                    onPinWidgetClick = { WidgetPinHelper.pinWidget(context) },
                    onRefreshFeed = {
                        viewModel.refreshFeed()
                        pagedFeed.refresh()
                    }
                )
            }

            // 2. Hero Card: Verse of the Day (Date-seeded, emerald gradient, Amiri Arabic, actions)
            item(key = "today_hero_verse") {
                val hero = uiState.heroVerse
                if (hero != null) {
                    TodayHeroVerseCard(
                        item = hero,
                        language = uiState.language,
                        isBookmarked = uiState.isHeroBookmarked,
                        onToggleBookmark = { viewModel.toggleBookmark(hero.id) },
                        onShareClick = { shareContentItem(context, hero, uiState.language) },
                        onPinWidgetClick = { WidgetPinHelper.pinWidget(context) },
                        onCardClick = { onNavigateToVerseDetail(hero.id) }
                    )
                } else {
                    CalmShimmerCard(modifier = Modifier.fillMaxWidth())
                }
            }

            // 3. Continue Reading Card (Surah, Ayah, Progress bar, Resume)
            item(key = "today_continue_reading") {
                ContinueReadingCard(
                    surahNumber = uiState.lastReadSurah,
                    surahName = uiState.lastReadSurahName,
                    ayahNumber = uiState.lastReadAyah,
                    totalVerses = uiState.totalVersesForSurah,
                    onContinueClick = {
                        onNavigateToSurahReader(uiState.lastReadSurah, uiState.lastReadAyah)
                    }
                )
            }

            // 4. Topics Grid (9 topics 3x3 icon-cards)
            item(key = "today_topics_grid") {
                TopicsGrid(
                    selectedTopicKey = uiState.selectedTopicKey,
                    onTopicSelected = { topicKey ->
                        viewModel.onTopicSelected(topicKey)
                    }
                )
            }

            // 5. Discover Feed Section Header + Segmented Filter Pill (Both / Quran / Hadith)
            item(key = "today_discover_header") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Discover",
                                style = typography.title.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Curated wisdom without repetition",
                                style = typography.caption,
                                color = colors.textSecondary
                            )
                        }

                        SegmentedFilterPill(
                            selectedType = uiState.selectedContentType,
                            onTypeSelected = { type ->
                                viewModel.onContentTypeSelected(type)
                            }
                        )
                    }
                }
            }

            // Loading State for initial feed refresh
            if (pagedFeed.loadState.refresh is LoadState.Loading) {
                items(count = 3, key = { index -> "feed_shimmer_$index" }) {
                    CalmShimmerCard()
                }
            }

            // Error State for initial feed refresh
            if (pagedFeed.loadState.refresh is LoadState.Error) {
                val error = (pagedFeed.loadState.refresh as LoadState.Error).error
                item(key = "feed_error") {
                    FeedErrorPlaceholder(
                        errorMessage = error.localizedMessage ?: "Unable to load feed content",
                        onRetry = { pagedFeed.retry() }
                    )
                }
            }

            // Empty State
            if (pagedFeed.loadState.refresh is LoadState.NotLoading && pagedFeed.itemCount == 0) {
                item(key = "feed_empty") {
                    FeedEmptyPlaceholder(
                        selectedTopic = uiState.selectedTopicKey,
                        onClearFilter = {
                            viewModel.onTopicSelected(null)
                        }
                    )
                }
            }

            // Infinite Discover Feed Items
            items(
                count = pagedFeed.itemCount,
                key = pagedFeed.itemKey { item -> item.id },
                contentType = pagedFeed.itemContentType { item -> item.type }
            ) { index ->
                val item = pagedFeed[index]
                if (item != null) {
                    val isBookmarked = uiState.bookmarkedIds.contains(item.id)
                    DiscoverFeedCard(
                        item = item,
                        language = uiState.language,
                        isBookmarked = isBookmarked,
                        onToggleBookmark = { viewModel.toggleBookmark(item.id) },
                        onShareClick = { shareContentItem(context, item, uiState.language) },
                        onClick = {
                            if (item.isQuran) {
                                onNavigateToVerseDetail(item.id)
                            } else {
                                onNavigateToHadithDetail(item.id)
                            }
                        },
                        onItemVisible = { visibleItem ->
                            viewModel.onItemVisible(visibleItem)
                        }
                    )
                }
            }

            // Append Loading Shimmer (when scrolling down)
            if (pagedFeed.loadState.append is LoadState.Loading) {
                item(key = "feed_append_loading") {
                    CalmShimmerCard()
                }
            }

            // Append Error State
            if (pagedFeed.loadState.append is LoadState.Error) {
                item(key = "feed_append_error") {
                    FeedErrorPlaceholder(
                        errorMessage = "Could not load more items",
                        onRetry = { pagedFeed.retry() }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedEmptyPlaceholder(
    selectedTopic: String?,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = NoorSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No items found",
            style = typography.subtitle.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(NoorSpacing.s))
        Text(
            text = if (selectedTopic != null) "No content currently matches the \"$selectedTopic\" topic filter."
            else "You have seen all recent items in this view.",
            style = typography.body.copy(fontSize = 14.sp),
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = NoorSpacing.l)
        )
        if (selectedTopic != null) {
            Spacer(modifier = Modifier.height(NoorSpacing.m))
            OutlinedButton(
                onClick = onClearFilter,
                shape = com.poswal.noor.core.designsystem.NoorShapes.chip,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
            ) {
                Text(text = "Clear Filter")
            }
        }
    }
}

@Composable
private fun FeedErrorPlaceholder(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = NoorSpacing.l),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Something went wrong",
            style = typography.subtitle.copy(fontWeight = FontWeight.SemiBold),
            color = colors.error
        )
        Spacer(modifier = Modifier.height(NoorSpacing.s))
        Text(
            text = errorMessage,
            style = typography.caption,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(NoorSpacing.m))
        OutlinedButton(
            onClick = onRetry,
            shape = com.poswal.noor.core.designsystem.NoorShapes.chip
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = colors.primary
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(text = "Try Again", color = colors.primary)
        }
    }
}

private fun shareContentItem(
    context: Context,
    item: ContentItemEntity,
    language: com.poswal.noor.domain.model.AppLanguage
) {
    val translation = item.translationFor(language) ?: item.translationEn
    val reference = item.reference

    val shareBody = buildString {
        appendLine(item.arabic)
        appendLine()
        if (!translation.isNullOrBlank()) {
            appendLine(translation)
            appendLine()
        }
        appendLine("— $reference")
        append("Poswal Noor · 100% Offline Islamic Companion")
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareBody)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share via"))
}
