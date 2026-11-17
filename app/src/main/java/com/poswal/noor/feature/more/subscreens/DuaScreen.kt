package com.poswal.noor.feature.more.subscreens

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmCard
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.domain.repository.ContentRepository
import com.poswal.noor.ui.dua.DuaCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuaScreen(
    repository: ContentRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    val allDuas by repository.getDuas().collectAsState(initial = emptyList())
    var selectedCategory by remember { mutableStateOf(DuaCategory.ALL) }

    val filteredDuas = remember(allDuas, selectedCategory) {
        when (selectedCategory) {
            DuaCategory.ALL -> allDuas
            DuaCategory.MORNING_EVENING -> allDuas.filter {
                it.translationEn.contains("morning", ignoreCase = true) ||
                        it.translationEn.contains("evening", ignoreCase = true) ||
                        it.arabic.contains("أصبحنا") || it.arabic.contains("أمسينا")
            }.ifEmpty { allDuas.take(20) }
            DuaCategory.AFTER_PRAYER -> allDuas.filter {
                it.translationEn.contains("prayer", ignoreCase = true) ||
                        it.translationEn.contains("salah", ignoreCase = true) ||
                        it.arabic.contains("صلاة")
            }.ifEmpty { allDuas.take(20) }
            DuaCategory.FORGIVENESS -> allDuas.filter {
                it.translationEn.contains("forgive", ignoreCase = true) ||
                        it.translationEn.contains("repent", ignoreCase = true) ||
                        it.arabic.contains("غفر") || it.arabic.contains("توب")
            }.ifEmpty { allDuas.take(25) }
            DuaCategory.HARDSHIP -> allDuas.filter {
                it.topic == "sabr" ||
                        it.translationEn.contains("patience", ignoreCase = true) ||
                        it.translationEn.contains("difficulty", ignoreCase = true)
            }.ifEmpty { allDuas.take(25) }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hisnul Muslim Duas",
                            style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "${filteredDuas.size} authentic supplications",
                            style = typography.caption.copy(fontSize = 11.sp),
                            color = colors.textSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = NoorSpacing.l, vertical = NoorSpacing.s),
                horizontalArrangement = Arrangement.spacedBy(NoorSpacing.s)
            ) {
                DuaCategory.entries.forEach { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) colors.primary else colors.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) colors.primary else colors.outline
                        ),
                        modifier = Modifier.clickable { selectedCategory = category }
                    ) {
                        Text(
                            text = category.title,
                            style = typography.caption.copy(
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) colors.onPrimary else colors.textPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            // Duas List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = NoorSpacing.l,
                    end = NoorSpacing.l,
                    top = NoorSpacing.s,
                    bottom = NoorSpacing.xxl
                ),
                verticalArrangement = Arrangement.spacedBy(NoorSpacing.m)
            ) {
                items(
                    items = filteredDuas,
                    key = { it.id }
                ) { dua ->
                    DuaItemCard(
                        item = dua,
                        onShare = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${dua.arabic}\n\n${dua.translationEn}\n\n— ${dua.reference} (via Poswal Noor)"
                                )
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Supplication"))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DuaItemCard(
    item: ContentItemEntity,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val haptic = LocalHapticFeedback.current

    // Repetition counter (defaults to 3x or 1x)
    val defaultTarget = if (item.arabic.length < 100) 3 else 1
    var remainingCount by remember(item.id) { mutableIntStateOf(defaultTarget) }

    CalmCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NoorSpacing.l)
        ) {
            // Header Row: Reference & Repetition Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = item.reference,
                        style = typography.caption.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Interactive Counter Pill
                val isCompleted = remainingCount == 0
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCompleted) colors.primary else colors.surfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCompleted) colors.primary else colors.outline
                    ),
                    modifier = Modifier.clickable {
                        if (remainingCount > 0) {
                            remainingCount -= 1
                            if (remainingCount == 0) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        } else {
                            remainingCount = defaultTarget
                        }
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = colors.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Done",
                                style = typography.caption.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = colors.onPrimary
                            )
                        } else {
                            Text(
                                text = "Repeat: ${remainingCount}x",
                                style = typography.caption.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // Arabic Supplication
            Text(
                text = item.arabic,
                style = typography.arabicQuran.copy(
                    fontSize = 22.sp,
                    lineHeight = 42.sp
                ),
                color = colors.textPrimary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(NoorSpacing.s))

            // English Translation
            Text(
                text = item.translationEn,
                style = typography.body.copy(
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                ),
                color = colors.textSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(NoorSpacing.s))

            // Footer Share Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
