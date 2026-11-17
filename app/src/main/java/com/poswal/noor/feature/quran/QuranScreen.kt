package com.poswal.noor.feature.quran

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmCard
import com.poswal.noor.core.designsystem.components.CalmShimmerCard
import com.poswal.noor.feature.quran.components.JuzCard
import com.poswal.noor.feature.quran.components.SurahCard

@Composable
fun QuranScreen(
    onNavigateToSurahReader: (surahNumber: Int, ayahNumber: Int) -> Unit,
    viewModel: QuranViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val uiState by viewModel.uiState.collectAsState()

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
                bottom = 100.dp // Floating nav bar clearance
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Header with Title & Search Toggle
            item(key = "quran_header") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Quran",
                                    style = typography.title.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                                    color = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "القرآن",
                                    style = typography.arabicQuran.copy(fontSize = 20.sp, lineHeight = 22.sp),
                                    color = colors.accent
                                )
                            }
                            Text(
                                text = "114 Surahs · 30 Juz · Authentic Tanzil Scripture",
                                style = typography.caption,
                                color = colors.textSecondary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleSearch(!uiState.isSearchActive) }
                        ) {
                            Icon(
                                imageVector = if (uiState.isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search",
                                tint = colors.textPrimary
                            )
                        }
                    }
                }
            }

            // Search Bar (if search is active)
            if (uiState.isSearchActive) {
                item(key = "quran_search_field") {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = {
                            Text(
                                text = "Search by Surah name, number, or meaning...",
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

            // 2. Segmented Pill Control: Surah / Juz
            item(key = "quran_segmented_tabs") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(NoorShapes.pill)
                        .background(colors.surfaceElevated)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf(QuranTab.SURAH to "Surah (114)", QuranTab.JUZ to "Juz (30)")
                    tabs.forEach { (tab, label) ->
                        val isSelected = (uiState.selectedTab == tab)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(NoorShapes.pill)
                                .background(if (isSelected) colors.primary else Color.Transparent)
                                .clickable { viewModel.selectTab(tab) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = typography.body.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                ),
                                color = if (isSelected) colors.onPrimary else colors.textSecondary
                            )
                        }
                    }
                }
            }

            // 3. Continue Reading Banner (when not searching and in Surah tab)
            if (!uiState.isSearchActive && uiState.selectedTab == QuranTab.SURAH) {
                item(key = "quran_continue_banner") {
                    CalmCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onNavigateToSurahReader(uiState.lastReadSurah, uiState.lastReadAyah)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(NoorSpacing.m),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colors.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(NoorSpacing.m))

                                Column {
                                    Text(
                                        text = "Continue Reading",
                                        style = typography.caption.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primary
                                        )
                                    )
                                    Text(
                                        text = "${uiState.lastReadSurahName} · Ayah ${uiState.lastReadAyah}",
                                        style = typography.body.copy(fontWeight = FontWeight.Bold),
                                        color = colors.textPrimary
                                    )
                                }
                            }

                            Text(
                                text = "Resume →",
                                style = typography.caption.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primary
                                )
                            )
                        }
                    }
                }
            }

            // 4. Loading Shimmer
            if (uiState.isLoading) {
                items(count = 6, key = { "quran_shimmer_$it" }) {
                    CalmShimmerCard(modifier = Modifier.fillMaxWidth())
                }
            }

            // 5. Surah List
            if (uiState.selectedTab == QuranTab.SURAH) {
                if (uiState.filteredSurahs.isEmpty() && !uiState.isLoading) {
                    item(key = "empty_surahs") {
                        EmptyResultBox(query = uiState.searchQuery)
                    }
                }

                items(
                    items = uiState.filteredSurahs,
                    key = { surah -> "surah_${surah.number}" }
                ) { surah ->
                    SurahCard(
                        surah = surah,
                        isLastRead = (surah.number == uiState.lastReadSurah),
                        onClick = {
                            val startAyah = if (surah.number == uiState.lastReadSurah) uiState.lastReadAyah else 1
                            onNavigateToSurahReader(surah.number, startAyah)
                        }
                    )
                }
            }

            // 6. Juz List
            if (uiState.selectedTab == QuranTab.JUZ) {
                if (uiState.filteredJuzList.isEmpty() && !uiState.isLoading) {
                    item(key = "empty_juz") {
                        EmptyResultBox(query = uiState.searchQuery)
                    }
                }

                items(
                    items = uiState.filteredJuzList,
                    key = { juz -> "juz_${juz.number}" }
                ) { juz ->
                    JuzCard(
                        juz = juz,
                        onClick = {
                            onNavigateToSurahReader(juz.startSurahNumber, juz.startAyah)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyResultBox(query: String, modifier: Modifier = Modifier) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = NoorSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No results found",
            style = typography.subtitle.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "No match for \"$query\". Check spelling or try a Surah number.",
            style = typography.body.copy(fontSize = 13.sp),
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}
