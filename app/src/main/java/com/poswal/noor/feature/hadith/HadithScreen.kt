package com.poswal.noor.feature.hadith

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.paging.compose.itemKey
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmShimmerCard
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.feature.hadith.components.HadithActionSheet
import com.poswal.noor.feature.hadith.components.HadithBookCard
import com.poswal.noor.feature.hadith.components.HadithCollectionCard
import com.poswal.noor.feature.hadith.components.HadithFeedCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithScreen(
    onNavigateBackToHome: () -> Unit = {},
    viewModel: HadithViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val uiState by viewModel.uiState.collectAsState()
    val pagedHadiths = viewModel.pagedHadiths.collectAsLazyPagingItems()

    var selectedHadithForAction by remember { mutableStateOf<ContentItemEntity?>(null) }

    // Intercept back navigation inside Hadith hierarchy
    BackHandler(enabled = uiState.selectedCollection != null) {
        val handled = viewModel.navigateBack()
        if (!handled) {
            onNavigateBackToHome()
        }
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val titleText = when {
                            uiState.selectedBook != null -> uiState.selectedBook!!.bookName.ifBlank { "Book ${uiState.selectedBook!!.bookNumber}" }
                            uiState.selectedCollection != null -> uiState.selectedCollection!!.titleEnglish
                            else -> "Hadith Collections"
                        }
                        val subtitleText = when {
                            uiState.selectedBook != null -> "${uiState.selectedCollection?.titleEnglish} · Book ${uiState.selectedBook!!.bookNumber}"
                            uiState.selectedCollection != null -> "${uiState.selectedCollection!!.totalBooks} Books · ${uiState.selectedCollection!!.totalHadiths} Hadiths"
                            else -> "Sahih al-Bukhari & Sahih Muslim"
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = titleText,
                                style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                color = colors.textPrimary,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = subtitleText,
                            style = typography.caption.copy(fontSize = 11.sp),
                            color = colors.textSecondary
                        )
                    }
                },
                navigationIcon = {
                    if (uiState.selectedCollection != null) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colors.textPrimary
                            )
                        }
                    }
                },
                actions = {
                    if (uiState.selectedCollection != null && uiState.selectedBook == null) {
                        IconButton(onClick = { viewModel.toggleSearch(!uiState.isSearchActive) }) {
                            Icon(
                                imageVector = if (uiState.isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search",
                                tint = colors.textPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surfaceElevated.copy(alpha = 0.95f)
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colors.background)
        ) {
            Crossfade(
                targetState = when {
                    uiState.selectedBook != null -> 3 // Hadith List Level
                    uiState.selectedCollection != null -> 2 // Books List Level
                    else -> 1 // Collections Overview Level
                },
                label = "hadithNavigationCrossfade"
            ) { level ->
                when (level) {
                    // Level 1: Collections Overview (Bukhari & Muslim)
                    1 -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 20.dp,
                                end = 20.dp,
                                top = 16.dp,
                                bottom = 100.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(NoorSpacing.l)
                        ) {
                            item(key = "hadith_overview_header") {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Authentic Sunnah",
                                        style = typography.caption.copy(fontWeight = FontWeight.Bold, color = colors.primary)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Canonical Sahih Works",
                                        style = typography.display.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                                        color = colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "14,060 rigorously verified prophetic traditions with authentic Arabic scriptures and citations.",
                                        style = typography.body.copy(fontSize = 13.sp),
                                        color = colors.textSecondary
                                    )
                                }
                            }

                            items(
                                items = uiState.collections,
                                key = { col -> col.key }
                            ) { collection ->
                                HadithCollectionCard(
                                    collection = collection,
                                    onClick = { viewModel.selectCollection(collection) }
                                )
                            }
                        }
                    }

                    // Level 2: Books List for Selected Collection
                    2 -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 20.dp,
                                end = 20.dp,
                                top = 16.dp,
                                bottom = 100.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (uiState.isSearchActive) {
                                item(key = "book_search_field") {
                                    OutlinedTextField(
                                        value = uiState.searchQuery,
                                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                                        placeholder = {
                                            Text(
                                                text = "Search books by name or number...",
                                                style = typography.body.copy(fontSize = 14.sp)
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = NoorShapes.card,
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = colors.surface,
                                            unfocusedContainerColor = colors.surface,
                                            focusedIndicatorColor = colors.primary,
                                            unfocusedIndicatorColor = colors.outline
                                        )
                                    )
                                }
                            }

                            if (uiState.isLoadingBooks) {
                                items(count = 6) {
                                    CalmShimmerCard(modifier = Modifier.fillMaxWidth())
                                }
                            } else {
                                if (uiState.filteredBooks.isEmpty()) {
                                    item(key = "empty_books") {
                                        EmptyBooksPlaceholder(query = uiState.searchQuery)
                                    }
                                }

                                items(
                                    items = uiState.filteredBooks,
                                    key = { book -> "book_${book.bookNumber}" }
                                ) { book ->
                                    HadithBookCard(
                                        book = book,
                                        onClick = { viewModel.selectBook(book) }
                                    )
                                }
                            }
                        }
                    }

                    // Level 3: Hadith List (Paging 3 Infinite Scroll)
                    3 -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 20.dp,
                                end = 20.dp,
                                top = 16.dp,
                                bottom = 100.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(NoorSpacing.l)
                        ) {
                            if (pagedHadiths.loadState.refresh is LoadState.Loading) {
                                items(count = 3) {
                                    CalmShimmerCard(modifier = Modifier.fillMaxWidth())
                                }
                            }

                            items(
                                count = pagedHadiths.itemCount,
                                key = pagedHadiths.itemKey { it.id }
                            ) { index ->
                                val item = pagedHadiths[index]
                                if (item != null) {
                                    val isBookmarked = uiState.bookmarkedIds.contains(item.id)
                                    HadithFeedCard(
                                        item = item,
                                        isBookmarked = isBookmarked,
                                        onToggleBookmark = { viewModel.toggleBookmark(item.id) },
                                        onShareClick = { shareHadith(context, item) },
                                        onActionMenuClick = { selectedHadithForAction = item },
                                        onClick = { selectedHadithForAction = item }
                                    )
                                }
                            }

                            if (pagedHadiths.loadState.append is LoadState.Loading) {
                                item(key = "append_shimmer") {
                                    CalmShimmerCard(modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Hadith Action Sheet
    selectedHadithForAction?.let { hadith ->
        val isBookmarked = uiState.bookmarkedIds.contains(hadith.id)
        HadithActionSheet(
            item = hadith,
            isBookmarked = isBookmarked,
            onDismiss = { selectedHadithForAction = null },
            onToggleBookmark = { viewModel.toggleBookmark(hadith.id) },
            onCopyText = {
                val textToCopy = buildString {
                    if (hadith.arabic.isNotBlank()) {
                        appendLine(hadith.arabic)
                        appendLine()
                    }
                    appendLine(hadith.translationEn)
                    appendLine("— ${hadith.reference}")
                }
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Hadith Text", textToCopy))
                Toast.makeText(context, "Hadith copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            onShareText = {
                shareHadith(context, hadith)
            },
            onSaveNote = { note ->
                viewModel.saveNote(hadith.id, note)
                Toast.makeText(context, "Note saved", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun EmptyBooksPlaceholder(query: String, modifier: Modifier = Modifier) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = NoorSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No books found",
            style = typography.subtitle.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "No book matches \"$query\".",
            style = typography.body.copy(fontSize = 13.sp),
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

private fun shareHadith(context: Context, item: ContentItemEntity) {
    val shareBody = buildString {
        if (item.arabic.isNotBlank()) {
            appendLine(item.arabic)
            appendLine()
        }
        appendLine(item.translationEn)
        appendLine()
        appendLine("— ${item.reference}")
        append("Poswal Noor · 100% Offline Islamic Companion")
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareBody)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share Hadith via"))
}
