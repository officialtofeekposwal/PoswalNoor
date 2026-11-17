package com.poswal.noor.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.ui.detail.DetailScreen
import com.poswal.noor.ui.detail.DetailViewModel
import com.poswal.noor.ui.home.HomeScreen
import com.poswal.noor.ui.home.HomeViewModel
import com.poswal.noor.ui.navigation.Screen
import com.poswal.noor.ui.onboarding.LanguageScreen
import com.poswal.noor.ui.settings.SettingsScreen
import com.poswal.noor.ui.settings.SettingsViewModel
import com.poswal.noor.ui.theme.NoorTheme
import com.poswal.noor.worker.WidgetUpdateWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Schedule periodic widget refresh worker based on saved preferences
        val initialPrefs = runBlocking { preferencesRepository.getPreferencesSync() }
        WidgetUpdateWorker.schedule(applicationContext, initialPrefs.autoRefreshHours)

        val widgetContentId = intent?.getLongExtra("contentId", -1L)?.takeIf { it > 0 }

        setContent {
            NoorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NoorAppNavigation(
                        preferencesRepository = preferencesRepository,
                        initialDetailId = widgetContentId
                    )
                }
            }
        }
    }
}

@Composable
fun NoorAppNavigation(
    preferencesRepository: UserPreferencesRepository,
    initialDetailId: Long? = null
) {
    val navController = rememberNavController()
    val prefs by preferencesRepository.userPreferencesFlow.collectAsState(initial = null)

    LaunchedEffect(initialDetailId) {
        if (initialDetailId != null && initialDetailId > 0) {
            navController.navigate(Screen.Detail.createRoute(initialDetailId))
        }
    }

    if (prefs == null) return

    val startDestination = if (prefs!!.isOnboardingCompleted) {
        Screen.Home.route
    } else {
        Screen.Onboarding.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            com.poswal.noor.ui.onboarding.OnboardingScreen(
                currentLanguage = prefs!!.language,
                onFinishOnboarding = { selectedLang ->
                    runBlocking {
                        preferencesRepository.setLanguage(selectedLang)
                        preferencesRepository.setOnboardingCompleted(true)
                    }
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(selectedLang.tag))
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToQuran = {
                    navController.navigate(Screen.Quran.route)
                },
                onNavigateToHadith = {
                    navController.navigate(Screen.Hadith.route)
                },
                onNavigateToDua = {
                    navController.navigate(Screen.Dua.route)
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToBookmarks = {
                    navController.navigate(Screen.Bookmarks.route)
                },
                onNavigateToTasbih = {
                    navController.navigate(Screen.Tasbih.route)
                },
                onNavigateToPrayerTimes = {
                    navController.navigate(Screen.PrayerTimes.route)
                }
            )
        }

        composable(Screen.Quran.route) {
            val quranViewModel: com.poswal.noor.ui.quran.QuranViewModel = hiltViewModel()
            com.poswal.noor.ui.quran.QuranReaderScreen(
                viewModel = quranViewModel,
                onNavigateToSurah = { surahNumber ->
                    navController.navigate(Screen.SurahDetail.createRoute(surahNumber))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SurahDetail.route,
            arguments = listOf(
                navArgument("surahNumber") { type = NavType.IntType }
            )
        ) {
            val surahDetailViewModel: com.poswal.noor.ui.quran.SurahDetailViewModel = hiltViewModel()
            com.poswal.noor.ui.quran.SurahDetailScreen(
                viewModel = surahDetailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Hadith.route) {
            val hadithViewModel: com.poswal.noor.ui.hadith.HadithViewModel = hiltViewModel()
            com.poswal.noor.ui.hadith.HadithBrowserScreen(
                viewModel = hadithViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            val searchViewModel: com.poswal.noor.ui.search.SearchViewModel = hiltViewModel()
            com.poswal.noor.ui.search.SearchScreen(
                viewModel = searchViewModel,
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Dua.route) {
            val duaViewModel: com.poswal.noor.ui.dua.DuaViewModel = hiltViewModel()
            com.poswal.noor.ui.dua.DuaScreen(
                viewModel = duaViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Bookmarks.route) {
            val bookmarksViewModel: com.poswal.noor.ui.bookmarks.BookmarksViewModel = hiltViewModel()
            com.poswal.noor.ui.bookmarks.BookmarksScreen(
                viewModel = bookmarksViewModel,
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Tasbih.route) {
            val tasbihViewModel: com.poswal.noor.ui.tasbih.TasbihViewModel = hiltViewModel()
            com.poswal.noor.ui.tasbih.TasbihScreen(
                viewModel = tasbihViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PrayerTimes.route) {
            val prayerViewModel: com.poswal.noor.ui.prayer.PrayerTimesViewModel = hiltViewModel()
            com.poswal.noor.ui.prayer.PrayerTimesScreen(
                viewModel = prayerViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("contentId") { type = NavType.LongType }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "poswalnoor://content?id={contentId}" }
            )
        ) {
            val detailViewModel: DetailViewModel = hiltViewModel()
            DetailScreen(
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
