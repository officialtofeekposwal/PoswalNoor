package com.poswal.noor.ui

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.poswal.noor.core.designsystem.NoorTheme
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.FloatingGlassNavBar
import com.poswal.noor.core.designsystem.components.NoorNavTab
import com.poswal.noor.feature.hadith.HadithScreen
import com.poswal.noor.feature.quran.QuranReaderScreen
import com.poswal.noor.feature.quran.QuranScreen
import com.poswal.noor.feature.saved.SavedScreen
import com.poswal.noor.feature.showcase.DesignShowcaseScreen
import com.poswal.noor.feature.today.TodayScreen
import com.poswal.noor.ui.components.TabPlaceholderScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            NoorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NoorThemeTokens.colors.background
                ) {
                    MainAppScaffold()
                }
            }
        }
    }
}

@Composable
fun MainAppScaffold() {
    var currentTab by remember { mutableStateOf(NoorNavTab.TODAY) }
    var showDesignShowcase by remember { mutableStateOf(false) }

    // Active Quran reader navigation target: Pair(surahNumber, startAyah)
    var activeReaderTarget by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    // Predictive / system back handling
    BackHandler(enabled = activeReaderTarget != null || showDesignShowcase || currentTab != NoorNavTab.TODAY) {
        if (showDesignShowcase) {
            showDesignShowcase = false
        } else if (activeReaderTarget != null) {
            activeReaderTarget = null
        } else if (currentTab != NoorNavTab.TODAY) {
            currentTab = NoorNavTab.TODAY
        }
    }

    if (showDesignShowcase) {
        DesignShowcaseScreen(
            currentTab = currentTab,
            onTabSelected = {
                currentTab = it
                showDesignShowcase = false
            }
        )
        return
    }

    // If an ayah or surah is open in the full-screen reader
    if (activeReaderTarget != null) {
        val (surah, ayah) = activeReaderTarget!!
        QuranReaderScreen(
            surahNumber = surah,
            startAyah = ayah,
            onBackClick = { activeReaderTarget = null }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(targetState = currentTab, label = "tabCrossfade") { tab ->
            when (tab) {
                NoorNavTab.TODAY -> {
                    TodayScreen(
                        onNavigateToSearch = {
                            // Phase 5 Search
                        },
                        onNavigateToVerseDetail = { contentId ->
                            // Open in reader
                        },
                        onNavigateToSurahReader = { surah, ayah ->
                            currentTab = NoorNavTab.QURAN
                            activeReaderTarget = Pair(surah, ayah)
                        },
                        onNavigateToHadithDetail = { _ ->
                            currentTab = NoorNavTab.HADITH
                        }
                    )
                }
                NoorNavTab.QURAN -> {
                    QuranScreen(
                        onNavigateToSurahReader = { surah, ayah ->
                            activeReaderTarget = Pair(surah, ayah)
                        }
                    )
                }
                NoorNavTab.HADITH -> {
                    HadithScreen(
                        onNavigateBackToHome = {
                            currentTab = NoorNavTab.TODAY
                        }
                    )
                }
                NoorNavTab.SAVED -> {
                    SavedScreen(
                        onNavigateToSurahReader = { surah, ayah ->
                            currentTab = NoorNavTab.QURAN
                            activeReaderTarget = Pair(surah, ayah)
                        },
                        onNavigateToHadithDetail = { _ ->
                            currentTab = NoorNavTab.HADITH
                        }
                    )
                }
                NoorNavTab.MORE -> {
                    TabPlaceholderScreen(
                        title = "More & Settings",
                        subtitle = "Phase 6: Language switcher, theme tokens, Hisnul Muslim Duas, Tasbih & offline Hijri calendar.",
                        onShowDesignShowcase = { showDesignShowcase = true }
                    )
                }
            }
        }

        // Floating Glass Bottom Navigation Bar
        FloatingGlassNavBar(
            selectedTab = currentTab,
            onTabSelected = {
                currentTab = it
                activeReaderTarget = null
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
