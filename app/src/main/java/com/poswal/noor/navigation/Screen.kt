package com.poswal.noor.navigation

sealed class Screen(val route: String) {
    data object Today : Screen("today")
    data object Quran : Screen("quran")
    data object Hadith : Screen("hadith")
    data object Saved : Screen("saved")
    data object More : Screen("more")
    data object Showcase : Screen("showcase")
    
    data object Detail : Screen("detail/{contentId}") {
        fun createRoute(contentId: Long) = "detail/$contentId"
    }

    data object SurahDetail : Screen("quran/surah/{surahNumber}") {
        fun createRoute(surahNumber: Int) = "quran/surah/$surahNumber"
    }
}
