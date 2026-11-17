package com.poswal.noor.feature.more

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmCard
import com.poswal.noor.data.preferences.NoorThemeMode
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.repository.ContentRepository
import com.poswal.noor.feature.more.subscreens.DuaScreen
import com.poswal.noor.feature.more.subscreens.HijriCalendarSheet
import com.poswal.noor.feature.more.subscreens.TasbihScreen

enum class MoreSubscreen {
    NONE,
    DUA,
    TASBIH
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    contentRepository: ContentRepository,
    preferencesRepository: UserPreferencesRepository,
    viewModel: MoreViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val prefs by viewModel.userPreferences.collectAsState()

    var activeSubscreen by remember { mutableStateOf(MoreSubscreen.NONE) }
    var showHijriSheet by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    when (activeSubscreen) {
        MoreSubscreen.DUA -> {
            DuaScreen(
                repository = contentRepository,
                onNavigateBack = { activeSubscreen = MoreSubscreen.NONE }
            )
        }
        MoreSubscreen.TASBIH -> {
            TasbihScreen(
                preferencesRepository = preferencesRepository,
                onNavigateBack = { activeSubscreen = MoreSubscreen.NONE }
            )
        }
        MoreSubscreen.NONE -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = colors.background,
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = "More & Settings",
                                    style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Spiritual tools, preferences & authenticity",
                                    style = typography.caption.copy(fontSize = 11.sp),
                                    color = colors.textSecondary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
                    )
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = NoorSpacing.l,
                        end = NoorSpacing.l,
                        top = NoorSpacing.s,
                        bottom = 100.dp // Floating bottom bar clearance
                    ),
                    verticalArrangement = Arrangement.spacedBy(NoorSpacing.l)
                ) {
                    // SECTION 1: Spiritual Utilities
                    item {
                        Text(
                            text = "SPIRITUAL UTILITIES",
                            style = typography.caption.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = colors.primary
                        )
                        Spacer(modifier = Modifier.height(NoorSpacing.s))

                        Column(verticalArrangement = Arrangement.spacedBy(NoorSpacing.s)) {
                            UtilityNavigationCard(
                                icon = Icons.AutoMirrored.Filled.MenuBook,
                                title = "Hisnul Muslim Duas",
                                subtitle = "Daily supplications, morning/evening, and protection",
                                onClick = { activeSubscreen = MoreSubscreen.DUA }
                            )

                            UtilityNavigationCard(
                                icon = Icons.Default.TouchApp,
                                title = "Digital Tasbih",
                                subtitle = "Interactive dhikr dial with spring haptics & lap targets",
                                onClick = { activeSubscreen = MoreSubscreen.TASBIH }
                            )

                            UtilityNavigationCard(
                                icon = Icons.Default.CalendarMonth,
                                title = "Offline Hijri Calendar",
                                subtitle = "Algorithmic lunar dates & key Islamic occasions",
                                onClick = { showHijriSheet = true }
                            )
                        }
                    }

                    // SECTION 2: Reading & Appearance
                    item {
                        Text(
                            text = "APPEARANCE & READING",
                            style = typography.caption.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = colors.primary
                        )
                        Spacer(modifier = Modifier.height(NoorSpacing.s))

                        CalmCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(vertical = NoorSpacing.xs)) {
                                SettingClickableRow(
                                    icon = Icons.Default.DarkMode,
                                    title = "Theme",
                                    value = prefs?.themeMode?.title ?: "System Default",
                                    onClick = { showThemeDialog = true }
                                )

                                HorizontalDivider(color = colors.outline.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = NoorSpacing.l))

                                SettingClickableRow(
                                    icon = Icons.Default.Language,
                                    title = "Translation Language",
                                    value = prefs?.language?.nativeName ?: "English",
                                    onClick = { showLanguageDialog = true }
                                )

                                HorizontalDivider(color = colors.outline.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = NoorSpacing.l))

                                SettingClickableRow(
                                    icon = Icons.Default.FormatSize,
                                    title = "Arabic Font Size",
                                    value = "${(28f * (prefs?.fontSizeScale ?: 1.0f)).toInt()} sp",
                                    onClick = { showFontSizeDialog = true }
                                )
                            }
                        }
                    }

                    // SECTION 3: Authenticity & About
                    item {
                        Text(
                            text = "DATA SOURCES & INTEGRITY",
                            style = typography.caption.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = colors.primary
                        )
                        Spacer(modifier = Modifier.height(NoorSpacing.s))

                        CalmCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(NoorSpacing.l)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(NoorSpacing.s))
                                    Text(
                                        text = "100% Offline & Private",
                                        style = typography.subtitle.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                                        color = colors.textPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(NoorSpacing.xs))

                                Text(
                                    text = "Poswal Noor contains zero analytics, zero trackers, zero advertisements, and does not require android.permission.INTERNET.",
                                    style = typography.caption.copy(fontSize = 12.sp, lineHeight = 18.sp),
                                    color = colors.textSecondary
                                )

                                Spacer(modifier = Modifier.height(NoorSpacing.m))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showAboutDialog = true },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tanzil & Hadith Attributions",
                                        style = typography.caption.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primary
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Version & Footer
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = NoorSpacing.m),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Poswal Noor v2.0.0",
                                style = typography.caption.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Clean Architecture · Jetpack Compose · 100% Offline",
                                style = typography.caption.copy(fontSize = 10.sp),
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }

    // Hijri Calendar Sheet
    if (showHijriSheet) {
        HijriCalendarSheet(onDismiss = { showHijriSheet = false })
    }

    // Theme Mode Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text = "Select Theme",
                    style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            },
            text = {
                Column {
                    NoorThemeMode.entries.forEach { mode ->
                        val isSelected = prefs?.themeMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.setThemeMode(mode)
                                        showThemeDialog = false
                                    }
                                )
                                .padding(vertical = NoorSpacing.s),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = colors.primary)
                            )
                            Spacer(modifier = Modifier.width(NoorSpacing.s))
                            Text(
                                text = mode.title,
                                style = typography.body.copy(fontSize = 14.sp),
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = NoorShapes.card
        )
    }

    // Language Picker Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = "Translation Language",
                    style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            },
            text = {
                Column {
                    AppLanguage.entries.forEach { lang ->
                        val isSelected = prefs?.language == lang
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.setLanguage(lang)
                                        showLanguageDialog = false
                                    }
                                )
                                .padding(vertical = NoorSpacing.s),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = colors.primary)
                            )
                            Spacer(modifier = Modifier.width(NoorSpacing.s))
                            Column {
                                Text(
                                    text = lang.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = typography.body.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = lang.nativeName,
                                    style = typography.caption.copy(fontSize = 11.sp),
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = NoorShapes.card
        )
    }

    // Font Size Slider Dialog
    if (showFontSizeDialog) {
        var currentScale by remember { mutableFloatStateOf(prefs?.fontSizeScale ?: 1.0f) }

        AlertDialog(
            onDismissRequest = { showFontSizeDialog = false },
            title = {
                Text(
                    text = "Arabic Quran Font Size",
                    style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        style = typography.arabicQuran.copy(
                            fontSize = (28f * currentScale).sp,
                            lineHeight = (56f * currentScale).sp
                        ),
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = NoorSpacing.m)
                    )

                    Slider(
                        value = currentScale,
                        onValueChange = { currentScale = it },
                        valueRange = 0.8f..1.5f,
                        steps = 6,
                        colors = SliderDefaults.colors(
                            thumbColor = colors.primary,
                            activeTrackColor = colors.primary
                        )
                    )

                    Text(
                        text = "${(28f * currentScale).toInt()} sp (${(currentScale * 100).toInt()}%)",
                        style = typography.caption.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                        color = colors.primary
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setFontSizeScale(currentScale)
                        showFontSizeDialog = false
                    }
                ) {
                    Text("Apply", color = colors.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFontSizeDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = NoorShapes.card
        )
    }

    // About & Data Sources Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    text = "Official Data Sources & Terms",
                    style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(NoorSpacing.m)) {
                    Text(
                        text = "Holy Quran (Tanzil Project)",
                        style = typography.caption.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        color = colors.primary
                    )
                    Text(
                        text = "Permission is granted to copy and distribute verbatim copies of this Quran text, but changing the text is not allowed.\n\nThis Quran text can be used in any website or application, provided that its source (Tanzil Project) is clearly indicated, and a link is made to tanzil.net to enable users to keep track of changes.",
                        style = typography.caption.copy(fontSize = 11.sp, lineHeight = 16.sp),
                        color = colors.textSecondary
                    )

                    HorizontalDivider(color = colors.outline.copy(alpha = 0.5f))

                    Text(
                        text = "Hadith Collections (hadith-api)",
                        style = typography.caption.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        color = colors.primary
                    )
                    Text(
                        text = "Sahih al-Bukhari (7,050 records, 97 books) & Sahih Muslim (7,010 records, 56 books). Dataset repository licensed under The Unlicense (Public Domain dedication). English translations by Dr. M. Muhsin Khan and Abdul Hamid Siddiqui.",
                        style = typography.caption.copy(fontSize = 11.sp, lineHeight = 16.sp),
                        color = colors.textSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Understood", color = colors.primary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = colors.surface,
            shape = NoorShapes.card
        )
    }
}

@Composable
private fun UtilityNavigationCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    CalmCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NoorSpacing.l),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = colors.primaryContainer.copy(alpha = 0.6f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(NoorSpacing.m))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = typography.subtitle.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = typography.caption.copy(fontSize = 12.sp),
                    color = colors.textSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun SettingClickableRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = NoorSpacing.l, vertical = NoorSpacing.m),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(NoorSpacing.m))
            Text(
                text = title,
                style = typography.body.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                color = colors.textPrimary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = typography.caption.copy(fontSize = 13.sp, color = colors.primary, fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.width(NoorSpacing.xs))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = colors.outline,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
