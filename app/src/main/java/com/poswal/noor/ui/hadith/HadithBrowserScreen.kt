package com.poswal.noor.ui.hadith

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.translationFor
import com.poswal.noor.ui.components.glass.LiquidGlassCard
import com.poswal.noor.ui.theme.ArabicQuranTextStyle
import com.poswal.noor.ui.theme.HindiTranslationTextStyle
import com.poswal.noor.ui.theme.NoorEmerald
import com.poswal.noor.ui.theme.NoorEmeraldDeep
import com.poswal.noor.ui.theme.NoorGold
import com.poswal.noor.ui.theme.NoorIvory
import com.poswal.noor.ui.theme.UrduTranslationTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithBrowserScreen(
    viewModel: HadithViewModel,
    onNavigateBack: () -> Unit
) {
    val selectedCollection by viewModel.selectedCollection.collectAsState()
    val pagedHadiths = viewModel.pagedHadiths.collectAsLazyPagingItems()
    val language by viewModel.language.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hadith Collections",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "صحيح البخاري ومسلم — 14,060 Hadiths",
                            style = MaterialTheme.typography.labelSmall,
                            color = NoorEmerald
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Collection Selector Switch
            item {
                val selectedIndex = if (selectedCollection == "Sahih al-Bukhari") 0 else 1

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
                    val segmentWidth = maxWidth / 2
                    val pillOffset by androidx.compose.animation.core.animateDpAsState(
                        targetValue = segmentWidth * selectedIndex,
                        animationSpec = androidx.compose.animation.core.spring(
                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                        ),
                        label = "hadithPillOffset"
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
                        viewModel.collections.forEachIndexed { index, coll ->
                            val isSelected = selectedIndex == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(11.dp))
                                    .clickable { viewModel.selectCollection(coll) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = coll,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) NoorIvory else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Paged Items
            items(
                count = pagedHadiths.itemCount,
                key = pagedHadiths.itemKey { it.id },
                contentType = pagedHadiths.itemContentType { "HadithCard" }
            ) { index ->
                val hadith = pagedHadiths[index]
                if (hadith != null) {
                    HadithItemCard(
                        hadith = hadith,
                        language = language,
                        onToggleBookmark = { viewModel.toggleBookmark(hadith.id) },
                        isBookmarkedFlow = viewModel.isBookmarked(hadith.id)
                    )
                }
            }

            // Append loading state
            if (pagedHadiths.loadState.append is LoadState.Loading) {
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

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun HadithItemCard(
    hadith: ContentItemEntity,
    language: AppLanguage,
    onToggleBookmark: () -> Unit,
    isBookmarkedFlow: kotlinx.coroutines.flow.Flow<Boolean>
) {
    val isBookmarked by isBookmarkedFlow.collectAsState(initial = false)
    val context = LocalContext.current

    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 18.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Reference, Sahih Badge & Bookmark
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
                        text = hadith.reference,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = NoorEmerald,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NoorEmerald.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )

                    Text(
                        text = "Sahih",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NoorGold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NoorGold.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleBookmark,
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
                            val shareText = "${hadith.arabic}\n\n${hadith.translationFor(language) ?: hadith.translationEn}\n\n— ${hadith.reference}"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Hadith"))
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

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic Narration Text
            Text(
                text = hadith.arabic,
                style = ArabicQuranTextStyle,
                fontSize = 20.sp,
                lineHeight = 36.sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Localized Translation Text
            val translation = hadith.translationFor(language) ?: hadith.translationEn
            val translationStyle = when (language) {
                AppLanguage.URDU -> UrduTranslationTextStyle
                AppLanguage.HINDI -> HindiTranslationTextStyle
                else -> MaterialTheme.typography.bodyLarge
            }

            Text(
                text = translation,
                style = translationStyle,
                fontSize = 14.sp,
                lineHeight = if (language == AppLanguage.URDU) 28.sp else 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
