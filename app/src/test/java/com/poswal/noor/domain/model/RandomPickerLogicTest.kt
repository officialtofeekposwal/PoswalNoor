package com.poswal.noor.domain.model

import com.poswal.noor.data.local.entity.ContentItemEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RandomPickerLogicTest {

    private val sampleItems = listOf(
        ContentItemEntity(
            id = 1,
            type = "quran",
            arabic = "وَقُل رَّبِّ زِدْنِي عِلْمًا",
            translationEn = "And say: My Lord, increase me in knowledge.",
            reference = "Surah Taha 20:114",
            book = "quran",
            number = 2462,
            grade = "Mutawatir",
            topic = "ilm",
            wordCount = 5
        ),
        ContentItemEntity(
            id = 2,
            type = "hadith",
            arabic = "كُلُّ مَعْرُوفٍ صَدَقَةٌ",
            translationEn = "Every good deed is charity.",
            reference = "Sahih al-Bukhari 6021",
            book = "bukhari",
            number = 6021,
            grade = "Sahih",
            topic = "charity",
            wordCount = 3
        ),
        ContentItemEntity(
            id = 3,
            type = "hadith",
            arabic = "Long Hadith text ".repeat(30), // 90 words
            translationEn = "Long translation text",
            reference = "Sahih al-Bukhari 999",
            book = "bukhari",
            number = 999,
            grade = "Sahih",
            topic = "sabr",
            wordCount = 90
        )
    )

    @Test
    fun testWidgetFilter_respectsMaxWordCount40() {
        val widgetEligible = sampleItems.filter { it.wordCount <= 40 }
        assertEquals(2, widgetEligible.size)
        assertTrue(widgetEligible.all { it.wordCount <= 40 })
    }

    @Test
    fun testTopicFilter() {
        val ilmItems = sampleItems.filter { it.topic == "ilm" }
        assertEquals(1, ilmItems.size)
        assertEquals("ilm", ilmItems.first().topic)
    }

    @Test
    fun testContentTypeFilter() {
        val quranOnly = sampleItems.filter { it.type == "quran" }
        assertEquals(1, quranOnly.size)
        assertEquals("quran", quranOnly.first().type)

        val hadithOnly = sampleItems.filter { it.type == "hadith" }
        assertEquals(2, hadithOnly.size)
    }
}
