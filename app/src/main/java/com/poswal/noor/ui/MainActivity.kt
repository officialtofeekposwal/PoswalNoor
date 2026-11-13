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
            LanguageScreen(
                currentLanguage = prefs!!.language,
                onLanguageSelected = { selectedLang ->
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
                }
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
