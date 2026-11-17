package com.poswal.noor.ui.quran

import android.content.Intent
import kotlinx.coroutines.flow.Flow
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun SurahDetailScreen(
    viewModel: SurahDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val surah by viewModel.surah.collectAsState()
    val ayahs by viewModel.ayahs.collectAsState()
    val language by viewModel.language.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (surah != null) {
                        Column {
                            Text(
                                text = "${surah!!.number}. ${surah!!.nameEnglish}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${surah!!.nameTranslation} • ${surah!!.totalVerses} Ayahs",
                                style = MaterialTheme.typography.labelSmall,
                                color = NoorEmerald
                            )
                        }
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
        if (surah == null || ayahs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NoorEmerald)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Surah Ornate Header Banner
                item {
                    SurahHeaderCard(
                        surahNumber = surah!!.number,
                        nameAr = surah!!.nameArabic,
                        nameEn = surah!!.nameEnglish,
                        nameTranslation = surah!!.nameTranslation,
                        revelationPlace = surah!!.revelationType,
                        totalAyahs = surah!!.totalVerses
                    )
                }

                // Bismillah Calligraphy (Omitted for Surah At-Tawbah, surah 9)
                if (surah!!.number != 9) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                style = ArabicQuranTextStyle,
                                fontSize = 24.sp,
                                lineHeight = 44.sp,
                                textAlign = TextAlign.Center,
                                color = NoorEmeraldDeep
                            )
                        }
                    }
                }

                // Ayahs
                itemsIndexed(ayahs, key = { _, item -> item.id }) { index, ayah ->
                    AyahCard(
                        ayah = ayah,
                        ayahIndex = index + 1,
                        language = language,
                        surahName = surah!!.nameEnglish,
                        onAyahSeen = { num -> viewModel.onAyahVisible(num, surah!!.nameEnglish) },
                        onToggleBookmark = { viewModel.toggleBookmark(ayah.id) },
                        isBookmarkedFlow = viewModel.isBookmarked(ayah.id)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun SurahHeaderCard(
    surahNumber: Int,
    nameAr: String,
    nameEn: String,
    nameTranslation: String,
    revelationPlace: String,
    totalAyahs: Int
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 20.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = nameAr,
                style = ArabicQuranTextStyle,
                fontSize = 32.sp,
                lineHeight = 52.sp,
                fontWeight = FontWeight.Bold,
                color = NoorEmeraldDeep
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$surahNumber. $nameEn",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = nameTranslation,
                style = MaterialTheme.typography.bodyMedium,
                color = NoorGold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = revelationPlace.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = NoorEmerald,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NoorEmerald.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )

                Text(
                    text = "$totalAyahs Ayahs",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = NoorGold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NoorGold.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun AyahCard(
    ayah: ContentItemEntity,
    ayahIndex: Int,
    language: AppLanguage,
    surahName: String,
    onAyahSeen: (Int) -> Unit,
    onToggleBookmark: () -> Unit,
    isBookmarkedFlow: Flow<Boolean>
) {
    val isBookmarked by isBookmarkedFlow.collectAsState(initial = false)
    val context = LocalContext.current
    val ayahNum = ayah.ayah ?: ayahIndex

    LaunchedEffect(ayah.id) {
        onAyahSeen(ayahNum)
    }

    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 18.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Ayah Header Row: Ayah Number & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(NoorEmerald.copy(alpha = 0.12f))
                        .border(1.dp, NoorEmerald.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ayahNum.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = NoorEmerald
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
                            val shareText = "${ayah.arabic}\n\n${ayah.translationFor(language) ?: ayah.translationEn}\n\n— Surah $surahName, Ayah $ayahNum"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Ayah"))
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

            // Arabic Text
            Text(
                text = ayah.arabic,
                style = ArabicQuranTextStyle,
                fontSize = 22.sp,
                lineHeight = 42.sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Localized Translation Text
            val translation = ayah.translationFor(language) ?: ayah.translationEn
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
    }
}
