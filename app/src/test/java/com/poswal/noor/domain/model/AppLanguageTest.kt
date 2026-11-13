package com.poswal.noor.domain.model

import com.poswal.noor.data.local.entity.ContentItemEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppLanguageTest {

    private val sampleItemWithTranslations = ContentItemEntity(
        id = 1,
        type = "quran",
        arabic = "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
        translationEn = "Indeed, with hardship [will be] ease.",
        translationHi = "निश्चय ही कठिनाई के साथ सरलता है।",
        translationUr = "یقیناً تنگی کے ساتھ آسانی ہے۔",
        reference = "Surah Ash-Sharh 94:6",
        book = "quran",
        number = 6088,
        grade = "Mutawatir",
        topic = "sabr",
        wordCount = 4
    )

    private val sampleItemMissingUrduAndHindi = ContentItemEntity(
        id = 2,
        type = "hadith",
        arabic = "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
        translationEn = "A Muslim is the one from whose tongue and hands the Muslims are safe.",
        translationHi = null,
        translationUr = null,
        reference = "Sahih al-Bukhari 10",
        book = "bukhari",
        number = 10,
        grade = "Sahih",
        topic = "akhlaq",
        wordCount = 8
    )

    @Test
    fun testTranslationFor_whenAllTranslationsPresent() {
        assertEquals(
            "Indeed, with hardship [will be] ease.",
            sampleItemWithTranslations.translationFor(AppLanguage.ENGLISH)
        )
        assertEquals(
            "निश्चय ही कठिनाई के साथ सरलता है।",
            sampleItemWithTranslations.translationFor(AppLanguage.HINDI)
        )
        assertEquals(
            "یقیناً تنگی کے ساتھ آسانی ہے۔",
            sampleItemWithTranslations.translationFor(AppLanguage.URDU)
        )
        // Arabic selected: NO translation line shown
        assertNull(sampleItemWithTranslations.translationFor(AppLanguage.ARABIC))

        // Hinglish translation
        assertEquals(
            "Indeed, with hardship [will be] ease.",
            sampleItemWithTranslations.translationFor(AppLanguage.HINGLISH)
        )
    }

    @Test
    fun testTranslationFor_fallbackToEnglish_whenHindiOrUrduMissing() {
        // Fallback for Hindi
        assertEquals(
            "A Muslim is the one from whose tongue and hands the Muslims are safe.",
            sampleItemMissingUrduAndHindi.translationFor(AppLanguage.HINDI)
        )
        assertTrue(sampleItemMissingUrduAndHindi.isFallbackTranslation(AppLanguage.HINDI))

        // Fallback for Urdu
        assertEquals(
            "A Muslim is the one from whose tongue and hands the Muslims are safe.",
            sampleItemMissingUrduAndHindi.translationFor(AppLanguage.URDU)
        )
        assertTrue(sampleItemMissingUrduAndHindi.isFallbackTranslation(AppLanguage.URDU))

        // Arabic still returns null
        assertNull(sampleItemMissingUrduAndHindi.translationFor(AppLanguage.ARABIC))
    }

    @Test
    fun testLanguageDirection() {
        assertFalse(AppLanguage.ENGLISH.isRtl)
        assertFalse(AppLanguage.HINDI.isRtl)
        assertTrue(AppLanguage.URDU.isRtl)
        assertTrue(AppLanguage.ARABIC.isRtl)
    }
}
