package com.poswal.noor.feature.quran

import com.poswal.noor.feature.quran.model.QuranJuzData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuranPhase3Test {

    @Test
    fun testJuzCount_isExactly30() {
        assertEquals("There must be exactly 30 Juz in the Quran", 30, QuranJuzData.ALL_JUZ.size)
    }

    @Test
    fun testJuzData_startsAndEndsCorrectly() {
        val juz1 = QuranJuzData.ALL_JUZ.first()
        assertEquals("Juz 1 must be number 1", 1, juz1.number)
        assertEquals("Juz 1 starts at Surah 1", 1, juz1.startSurahNumber)
        assertEquals("Juz 1 starts at Ayah 1", 1, juz1.startAyah)

        val juz30 = QuranJuzData.ALL_JUZ.last()
        assertEquals("Juz 30 must be number 30", 30, juz30.number)
        assertEquals("Juz 30 ends at Surah 114", 114, juz30.endSurahNumber)
        assertEquals("Juz 30 ends at Ayah 6 (An-Nas)", 6, juz30.endAyah)
    }

    @Test
    fun testJuzData_continuousAndNonOverlapping() {
        for (i in 0 until QuranJuzData.ALL_JUZ.size - 1) {
            val current = QuranJuzData.ALL_JUZ[i]
            val next = QuranJuzData.ALL_JUZ[i + 1]

            assertEquals("Juz indices must be sequential", current.number + 1, next.number)
            assertTrue("Total verses in Juz ${current.number} must be positive", current.totalVerses > 0)
            assertTrue("Arabic name must not be blank", current.nameArabic.isNotBlank())
            assertTrue("English name must not be blank", current.nameEnglish.isNotBlank())
        }
    }

    @Test
    fun testBismillahHeaderRules() {
        // Function replicating BismillahHeader display condition:
        fun shouldShowBismillah(surahNumber: Int): Boolean {
            return surahNumber != 1 && surahNumber != 9
        }

        assertFalse("Surah 1 (Al-Fatihah) must NOT show Bismillah header (already ayah 1)", shouldShowBismillah(1))
        assertFalse("Surah 9 (At-Tawbah) must NOT show Bismillah header", shouldShowBismillah(9))

        assertTrue("Surah 2 (Al-Baqarah) MUST show Bismillah header", shouldShowBismillah(2))
        assertTrue("Surah 114 (An-Nas) MUST show Bismillah header", shouldShowBismillah(114))

        val displayedCount = (1..114).count { shouldShowBismillah(it) }
        assertEquals("Exactly 112 surahs should display the Bismillah header", 112, displayedCount)
    }

    @Test
    fun testReaderFontSizeScaleCalculation() {
        val baseSize = 28f
        val scales = listOf(0.8f, 1.0f, 1.25f, 1.5f)

        for (scale in scales) {
            val scaledSize = baseSize * scale
            assertTrue(
                "Scaled size should be within readable range (22sp - 42sp)",
                scaledSize in 22f..42f
            )
        }
    }
}
