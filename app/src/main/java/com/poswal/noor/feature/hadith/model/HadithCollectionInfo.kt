package com.poswal.noor.feature.hadith.model

data class HadithCollectionInfo(
    val key: String,
    val titleEnglish: String,
    val titleArabic: String,
    val author: String,
    val totalHadiths: Int,
    val totalBooks: Int,
    val grading: String,
    val description: String
)

object HadithCollectionsData {
    val COLLECTIONS: List<HadithCollectionInfo> = listOf(
        HadithCollectionInfo(
            key = "Sahih al-Bukhari",
            titleEnglish = "Sahih al-Bukhari",
            titleArabic = "صحيح البخاري",
            author = "Imam Muhammad al-Bukhari (194–256 AH)",
            totalHadiths = 7050,
            totalBooks = 97,
            grading = "Sahih",
            description = "The most authentic book of Hadith in Islamic literature, compiled over sixteen years."
        ),
        HadithCollectionInfo(
            key = "Sahih Muslim",
            titleEnglish = "Sahih Muslim",
            titleArabic = "صحيح مسلم",
            author = "Imam Muslim ibn al-Hajjaj (204–261 AH)",
            totalHadiths = 7010,
            totalBooks = 56,
            grading = "Sahih",
            description = "Renowned for its rigorous authentication and thematic classification of prophetic traditions."
        )
    )
}
