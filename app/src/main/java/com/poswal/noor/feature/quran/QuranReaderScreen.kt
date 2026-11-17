package com.poswal.noor.feature.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmShimmerCard
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.domain.model.translationFor
import com.poswal.noor.feature.quran.components.AyahActionSheet
import com.poswal.noor.feature.quran.components.AyahItemCard
import com.poswal.noor.feature.quran.components.BismillahHeader
import com.poswal.noor.feature.quran.components.ReaderSettingsSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    surahNumber: Int,
    startAyah: Int = 1,
    onBackClick: () -> Unit,
    viewModel: QuranReaderViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    val uiState by viewModel.uiState.collectAsState()

    // Trigger load when surahNumber changes
    LaunchedEffect(surahNumber) {
        viewModel.loadSurah(surahNumber, startAyah)
    }

    val listState = rememberLazyListState()

    // Auto-scroll to startAyah on initial data load
    LaunchedEffect(uiState.ayahs.size, startAyah) {
        if (uiState.ayahs.isNotEmpty() && startAyah > 1) {
            val targetIndex = (startAyah - 1).coerceIn(0, uiState.ayahs.size - 1)
            // +1 offset for Bismillah header if present
            val headerOffset = if (surahNumber != 1 && surahNumber != 9) 1 else 0
            listState.scrollToItem(targetIndex + headerOffset)
        }
    }

    // Auto-save last read scroll position
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { index ->
            if (uiState.ayahs.isNotEmpty()) {
                val headerOffset = if (surahNumber != 1 && surahNumber != 9) 1 else 0
                val ayahIndex = (index - headerOffset).coerceIn(0, uiState.ayahs.size - 1)
                val currentAyahEntity = uiState.ayahs.getOrNull(ayahIndex)
                if (currentAyahEntity != null && currentAyahEntity.ayah != null) {
                    viewModel.onAyahScrolledTo(currentAyahEntity.ayah)
                }
            }
        }
    }

    var selectedAyahForAction by remember { mutableStateOf<ContentItemEntity?>(null) }
    var showReaderSettings by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    val surah = uiState.surah
                    if (surah != null) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = surah.nameEnglish,
                                    style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                    color = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = surah.nameArabic,
                                    style = typography.arabicQuran.copy(fontSize = 16.sp, lineHeight = 18.sp),
                                    color = colors.accent
                                )
                            }
                            Text(
                                text = "Surah ${surah.number} · ${surah.totalVerses} Verses · ${surah.revelationType}",
                                style = typography.caption.copy(fontSize = 11.sp),
                                color = colors.textSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showReaderSettings = true }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Reader Settings",
                            tint = colors.textPrimary
                        )
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
            if (uiState.isLoading) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(NoorSpacing.m)
                ) {
                    items(count = 5) {
                        CalmShimmerCard(modifier = Modifier.fillMaxWidth())
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 16.dp,
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(NoorSpacing.m)
                ) {
                    // Bismillah Header (Surah 1 and 9 excluded)
                    item(key = "bismillah_header") {
                        BismillahHeader(surahNumber = surahNumber)
                    }

                    // Ayahs List
                    itemsIndexed(
                        items = uiState.ayahs,
                        key = { _, ayah -> "ayah_${ayah.id}" }
                    ) { _, ayah ->
                        val isBookmarked = uiState.bookmarkedAyahIds.contains(ayah.id)
                        AyahItemCard(
                            item = ayah,
                            language = uiState.language,
                            isBookmarked = isBookmarked,
                            arabicFontSizeSp = uiState.arabicFontSize,
                            translationFontSizeSp = uiState.translationFontSize,
                            showTranslation = uiState.showTranslation,
                            lineSpacingMultiplier = uiState.lineSpacingMultiplier,
                            onAyahClick = { selectedAyahForAction = ayah },
                            onToggleBookmark = { viewModel.toggleBookmark(ayah.id) }
                        )
                    }
                }
            }
        }
    }

    // Ayah Action Sheet (Modal Bottom Sheet)
    selectedAyahForAction?.let { ayah ->
        val isBookmarked = uiState.bookmarkedAyahIds.contains(ayah.id)
        AyahActionSheet(
            item = ayah,
            isBookmarked = isBookmarked,
            onDismiss = { selectedAyahForAction = null },
            onToggleBookmark = { viewModel.toggleBookmark(ayah.id) },
            onCopyText = {
                val translation = ayah.translationFor(uiState.language) ?: ayah.translationEn
                val textToCopy = "${ayah.arabic}\n\n$translation\n— ${ayah.reference}"
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Ayah Text", textToCopy))
                Toast.makeText(context, "Ayah copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            onShareText = {
                val translation = ayah.translationFor(uiState.language) ?: ayah.translationEn
                val shareText = "${ayah.arabic}\n\n$translation\n— ${ayah.reference}\nPoswal Noor"
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share Ayah via"))
            },
            onSaveNote = { note ->
                viewModel.saveNote(ayah.id, note)
                Toast.makeText(context, "Note saved to ayah", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Reader Settings Sheet (Modal Bottom Sheet)
    if (showReaderSettings) {
        ReaderSettingsSheet(
            arabicFontSize = uiState.arabicFontSize,
            translationFontSize = uiState.translationFontSize,
            showTranslation = uiState.showTranslation,
            lineSpacingMultiplier = uiState.lineSpacingMultiplier,
            onArabicFontSizeChange = { viewModel.updateArabicFontSize(it) },
            onTranslationFontSizeChange = { viewModel.updateTranslationFontSize(it) },
            onToggleShowTranslation = { viewModel.toggleShowTranslation(it) },
            onLineSpacingMultiplierChange = { viewModel.updateLineSpacingMultiplier(it) },
            onDismiss = { showReaderSettings = false }
        )
    }
}
