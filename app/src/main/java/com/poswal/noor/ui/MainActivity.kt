package com.poswal.noor.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
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

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentTab) {
            NoorNavTab.TODAY -> {
                TodayScreen(
                    onNavigateToSearch = {
                        // Hooked up in Phase 5
                    },
                    onNavigateToVerseDetail = { _ ->
                        // Hooked up in Phase 3
                    },
                    onNavigateToSurahReader = { _, _ ->
                        // Hooked up in Phase 3
                    },
                    onNavigateToHadithDetail = { _ ->
                        // Hooked up in Phase 4
                    }
                )
            }
            NoorNavTab.QURAN -> {
                TabPlaceholderScreen(
                    title = "The Holy Quran",
                    subtitle = "Phase 3: Surah & Juz lists, ayah-by-ayah reader, Amiri typography & audio recitation.",
                    onShowDesignShowcase = { showDesignShowcase = true }
                )
            }
            NoorNavTab.HADITH -> {
                TabPlaceholderScreen(
                    title = "Sahih Hadith Collections",
                    subtitle = "Phase 4: Sahih al-Bukhari & Sahih Muslim authentic books, chapters & gradings.",
                    onShowDesignShowcase = { showDesignShowcase = true }
                )
            }
            NoorNavTab.SAVED -> {
                TabPlaceholderScreen(
                    title = "Saved & Bookmarks",
                    subtitle = "Phase 5: Custom collections, personal notes, offline search & SAF JSON backup.",
                    onShowDesignShowcase = { showDesignShowcase = true }
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

        // Floating Glass Bottom Navigation Bar
        FloatingGlassNavBar(
            selectedTab = currentTab,
            onTabSelected = { currentTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
