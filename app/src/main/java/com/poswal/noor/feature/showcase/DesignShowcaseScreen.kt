package com.poswal.noor.feature.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmCard
import com.poswal.noor.core.designsystem.components.CalmShimmerCard
import com.poswal.noor.core.designsystem.components.FloatingGlassNavBar
import com.poswal.noor.core.designsystem.components.NoorNavTab

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesignShowcaseScreen(
    currentTab: NoorNavTab = NoorNavTab.TODAY,
    onTabSelected: (NoorNavTab) -> Unit = {}
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    var showShimmer by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colors.background,
        bottomBar = {
            FloatingGlassNavBar(
                selectedTab = currentTab,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = NoorSpacing.xl, vertical = NoorSpacing.l),
            verticalArrangement = Arrangement.spacedBy(NoorSpacing.xl)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                        style = typography.arabicQuran.copy(fontSize = 24.sp, lineHeight = 44.sp),
                        color = colors.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(NoorSpacing.s))
                    Text(
                        text = "Poswal Noor Foundation",
                        style = typography.display.copy(fontSize = 26.sp),
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Phase 1: Calm Library Design System & Foundation",
                        style = typography.caption,
                        color = colors.textSecondary
                    )
                }
            }

            // 1. Color Palette Tokens
            item {
                CalmCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "Design Palette Tokens",
                            style = typography.title,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(NoorSpacing.m))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(NoorSpacing.m),
                            verticalArrangement = Arrangement.spacedBy(NoorSpacing.m)
                        ) {
                            ColorSwatch("Background", colors.background, colors.textPrimary, border = true)
                            ColorSwatch("Surface", colors.surface, colors.textPrimary, border = true)
                            ColorSwatch("Elevated", colors.surfaceElevated, colors.textPrimary)
                            ColorSwatch("Emerald", colors.primary, colors.onPrimary)
                            ColorSwatch("Gold", colors.accent, Color.Black)
                            ColorSwatch("Text Primary", colors.textPrimary, colors.surface)
                            ColorSwatch("Divider", colors.outline, colors.textPrimary)
                        }
                    }
                }
            }

            // 2. Multilingual Bundled Typography Scale
            item {
                CalmCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(NoorSpacing.m)) {
                        Text(
                            text = "Authentic Typography Scale",
                            style = typography.title,
                            color = colors.textPrimary
                        )

                        // Arabic Amiri Quran
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(NoorShapes.chip)
                                .background(colors.surfaceElevated)
                                .padding(NoorSpacing.m)
                        ) {
                            Column {
                                Text(
                                    text = "Arabic (Amiri Quran - 28sp / 2.0x Line Height)",
                                    style = typography.label,
                                    color = colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(NoorSpacing.s))
                                Text(
                                    text = "إِنَّ مَعَ ٱلْعُسْرِ يُسْرًا",
                                    style = typography.arabicQuran,
                                    color = colors.textPrimary,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Urdu Noto Nastaliq
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(NoorShapes.chip)
                                .background(colors.surfaceElevated)
                                .padding(NoorSpacing.m)
                        ) {
                            Column {
                                Text(
                                    text = "Urdu (Noto Nastaliq Urdu - 2.2x Line Height)",
                                    style = typography.label,
                                    color = colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(NoorSpacing.s))
                                Text(
                                    text = "بے شک مشکل کے ساتھ آسانی ہے۔",
                                    style = typography.urduNastaliq,
                                    color = colors.textPrimary,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Hindi Noto Sans Devanagari
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(NoorShapes.chip)
                                .background(colors.surfaceElevated)
                                .padding(NoorSpacing.m)
                        ) {
                            Column {
                                Text(
                                    text = "Hindi (Noto Sans Devanagari)",
                                    style = typography.label,
                                    color = colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(NoorSpacing.s))
                                Text(
                                    text = "निश्चय ही कठिनाई के साथ सरलता है।",
                                    style = typography.hindiDevanagari,
                                    color = colors.textPrimary
                                )
                            }
                        }

                        // English UI
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(NoorShapes.chip)
                                .background(colors.surfaceElevated)
                                .padding(NoorSpacing.m)
                        ) {
                            Column {
                                Text(
                                    text = "English (Inter / System Sans)",
                                    style = typography.label,
                                    color = colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(NoorSpacing.s))
                                Text(
                                    text = "Indeed, with hardship comes ease. (Surah Ash-Sharh 94:6)",
                                    style = typography.body,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 3. CalmCard & Motion
            item {
                CalmCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showShimmer = !showShimmer }
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CalmCard (Press to toggle Shimmer)",
                                style = typography.title,
                                color = colors.textPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(NoorShapes.pill)
                                    .background(colors.primaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Spring 0.97x",
                                    style = typography.label,
                                    color = colors.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(NoorSpacing.s))
                        Text(
                            text = "Cards employ 1dp soft outline borders and zero heavy shadows. Tap anywhere on this card to preview the spring feedback and toggle the shimmer skeleton state below.",
                            style = typography.body,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            // 4. Shimmer Skeleton State
            if (showShimmer) {
                item {
                    Text(
                        text = "Shimmer Skeleton Preview (No Spinners)",
                        style = typography.subtitle,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(NoorSpacing.s))
                    CalmShimmerCard()
                }
            }

            // Bottom Spacing for Floating Bar
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    name: String,
    color: Color,
    textColor: Color,
    border: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(width = 96.dp, height = 48.dp)
            .clip(NoorShapes.chip)
            .background(color)
            .then(
                if (border) Modifier.border(1.dp, NoorThemeTokens.colors.outline, NoorShapes.chip)
                else Modifier
            )
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = NoorThemeTokens.typography.label.copy(fontSize = 10.sp),
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}
