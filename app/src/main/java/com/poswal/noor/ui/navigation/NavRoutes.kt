package com.poswal.noor.ui.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Detail : Screen("detail/{contentId}") {
        fun createRoute(contentId: Long) = "detail/$contentId"
    }
    data object Settings : Screen("settings")
    data object Quran : Screen("quran")
    data object SurahDetail : Screen("surah/{surahNumber}") {
        fun createRoute(surahNumber: Int) = "surah/$surahNumber"
    }
    data object Hadith : Screen("hadith")
    data object Dua : Screen("dua")
    data object Search : Screen("search")
    data object Bookmarks : Screen("bookmarks")
    data object Tasbih : Screen("tasbih")
    data object Qibla : Screen("qibla")
    data object PrayerTimes : Screen("prayer_times")
}
