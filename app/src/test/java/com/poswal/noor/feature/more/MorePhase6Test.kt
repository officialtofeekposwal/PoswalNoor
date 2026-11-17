package com.poswal.noor.feature.more

import com.poswal.noor.data.preferences.NoorThemeMode
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.ui.tasbih.DhikrPreset
import com.poswal.noor.util.HijriCalendarHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class MorePhase6Test {

    @Test
    fun testNoorThemeMode_entries() {
        val modes = NoorThemeMode.entries
        assertEquals("Theme modes must be exactly System, Light, and Dark", 3, modes.size)
        assertTrue(modes.contains(NoorThemeMode.SYSTEM))
        assertTrue(modes.contains(NoorThemeMode.LIGHT))
        assertTrue(modes.contains(NoorThemeMode.DARK))
    }

    @Test
    fun testHijriCalendar_algorithmicCalculation() {
        val cal = Calendar.getInstance()
        val hijriDate = HijriCalendarHelper.getHijriDate(cal)

        assertTrue("Hijri day must be in 1..30", hijriDate.day in 1..30)
        assertTrue("Hijri month must be in 1..12", hijriDate.monthNumber in 1..12)
        assertTrue("Hijri year must be plausible Islamic era (> 1440 AH)", hijriDate.year >= 1440)
        assertTrue("Month English name must not be blank", hijriDate.monthNameEn.isNotBlank())
        assertTrue("Month Arabic name must not be blank", hijriDate.monthNameAr.isNotBlank())
        assertTrue("Formatted date must contain 'AH'", hijriDate.formattedDate.contains("AH"))
    }

    @Test
    fun testIslamicOccasions_completeness() {
        val occasions = HijriCalendarHelper.ISLAMIC_OCCASIONS
        assertTrue("Must have at least 8 major Islamic occasions", occasions.size >= 8)

        val titles = occasions.map { it.title }
        assertTrue("Must include Ramadan", titles.any { it.contains("Ramadan", ignoreCase = true) })
        assertTrue("Must include Eid al-Fitr", titles.any { it.contains("Eid al-Fitr", ignoreCase = true) })
        assertTrue("Must include Ashura", titles.any { it.contains("Ashura", ignoreCase = true) })
        assertTrue("Must include Laylat al-Qadr", titles.any { it.contains("Laylat al-Qadr", ignoreCase = true) })
    }

    @Test
    fun testDhikrPresets_structure() {
        val presets = listOf(
            DhikrPreset("سُبْحَانَ اللَّهِ", "SubhanAllah", "Glory be to Allah", 33),
            DhikrPreset("الْحَمْدُ لِلَّهِ", "Alhamdulillah", "Praise be to Allah", 33),
            DhikrPreset("اللَّهُ أَكْبَرُ", "Allahu Akbar", "Allah is the Greatest", 34),
            DhikrPreset("أَسْتَغْفِرُ اللَّهَ", "Astaghfirullah", "I seek forgiveness from Allah", 100),
            DhikrPreset("لَا إِلَٰهَ إِلَّا اللَّهُ", "La ilaha illallah", "None worthy of worship but Allah", 100),
            DhikrPreset("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", "SubhanAllahi wa bihamdihi", "Glory and praise to Allah", 100)
        )

        assertEquals(6, presets.size)
        for (preset in presets) {
            assertTrue("Arabic scripture must not be blank", preset.titleAr.isNotBlank())
            assertTrue("English title must not be blank", preset.titleEn.isNotBlank())
            assertTrue("Target must be positive", preset.target > 0)
        }
    }

    @Test
    fun testAppLanguages_fiveLanguagesSupported() {
        val langs = AppLanguage.entries
        assertEquals("Exactly 5 languages must be supported in Poswal Noor", 5, langs.size)

        val tags = langs.map { it.tag }
        assertTrue(tags.contains("en"))
        assertTrue(tags.contains("en-IN"))
        assertTrue(tags.contains("hi"))
        assertTrue(tags.contains("ur"))
        assertTrue(tags.contains("ar"))
    }
}
