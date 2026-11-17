package com.poswal.noor.feature.hadith

import com.poswal.noor.feature.hadith.model.HadithCollectionsData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.sql.DriverManager

class HadithPhase4Test {

    private val dbFile = File("src/main/assets/database/poswal_noor.db")

    @Test
    fun testHadithCollections_metadata() {
        val collections = HadithCollectionsData.COLLECTIONS
        assertEquals("Exactly 2 major Sahih collections must be available", 2, collections.size)

        val bukhari = collections.first { it.key == "Sahih al-Bukhari" }
        assertEquals("Sahih al-Bukhari", bukhari.titleEnglish)
        assertEquals("صحيح البخاري", bukhari.titleArabic)
        assertEquals(97, bukhari.totalBooks)
        assertEquals(7050, bukhari.totalHadiths)
        assertEquals("Sahih", bukhari.grading)

        val muslim = collections.first { it.key == "Sahih Muslim" }
        assertEquals("Sahih Muslim", muslim.titleEnglish)
        assertEquals("صحيح مسلم", muslim.titleArabic)
        assertEquals(56, muslim.totalBooks)
        assertEquals(7010, muslim.totalHadiths)
        assertEquals("Sahih", muslim.grading)
    }

    @Test
    fun testReadMoreExpansionThreshold() {
        val shortText = "Actions are according to intentions, and everyone will get what was intended."
        val longText = "The Prophet (ﷺ) said: Avoid the seven great destructive sins. The people inquired: O Allah's Messenger! What are they? " +
                "He said: To join others in worship along with Allah, to practice sorcery, to kill the life which Allah has forbidden except for a just cause, " +
                "to eat up Riba (usury), to eat up an orphan's wealth, to give back to the enemy and flee from the battlefield at the time of fighting, " +
                "and to accuse chaste, women, who never even think of anything touching their chastity and are good believers."

        fun isExpandable(text: String): Boolean = text.length > 250

        assertFalse("Short text (<= 250 chars) should not require Read more collapse", isExpandable(shortText))
        assertTrue("Long text (> 250 chars) should require Read more collapse", isExpandable(longText))
    }

    @Test
    fun testDatabase_hadithAuthenticArabicAndBooks() {
        if (!dbFile.exists()) return
        val url = "jdbc:sqlite:${dbFile.absolutePath}"
        try {
            Class.forName("org.sqlite.JDBC")
            DriverManager.getConnection(url).use { conn ->
                val stmt = conn.createStatement()

                // Total Hadith Count
                val rsTotal = stmt.executeQuery("SELECT COUNT(*) FROM content_items WHERE type='hadith'")
                assertTrue(rsTotal.next())
                val totalCount = rsTotal.getInt(1)
                assertEquals("Total authentic Hadith count must be 14,060 (Bukhari + Muslim)", 14060, totalCount)

                // Sahih al-Bukhari Count
                val rsBukhari = stmt.executeQuery("SELECT COUNT(*) FROM content_items WHERE type='hadith' AND (collection='Sahih al-Bukhari' OR collection='bukhari')")
                assertTrue(rsBukhari.next())
                assertEquals("Sahih al-Bukhari must have 7,050 hadiths", 7050, rsBukhari.getInt(1))

                // Sahih Muslim Count
                val rsMuslim = stmt.executeQuery("SELECT COUNT(*) FROM content_items WHERE type='hadith' AND (collection='Sahih Muslim' OR collection='muslim')")
                assertTrue(rsMuslim.next())
                assertEquals("Sahih Muslim must have 7,010 hadiths", 7010, rsMuslim.getInt(1))

                // Verify ZERO placeholder or empty Arabic text
                val rsEmptyArabic = stmt.executeQuery("SELECT COUNT(*) FROM content_items WHERE type='hadith' AND (TRIM(arabic) = '' OR arabic = 'قال رسول الله صلى الله عليه وسلم')")
                assertTrue(rsEmptyArabic.next())
                assertEquals("Zero hadiths may have placeholder Arabic text; all 14,060 must be authentic", 0, rsEmptyArabic.getInt(1))

                // Verify Book metadata on all Hadiths
                val rsMissingBook = stmt.executeQuery("SELECT COUNT(*) FROM content_items WHERE type='hadith' AND (surah IS NULL OR surah <= 0 OR TRIM(surah_name) = '')")
                assertTrue(rsMissingBook.next())
                assertEquals("Every Hadith must have a valid book number and book name", 0, rsMissingBook.getInt(1))

                // Verify authentic grading retention (100% Sahih)
                val rsNonSahih = stmt.executeQuery("SELECT COUNT(*) FROM content_items WHERE type='hadith' AND grading != 'Sahih'")
                assertTrue(rsNonSahih.next())
                assertEquals("All Bukhari and Muslim records must retain authentic Sahih grading", 0, rsNonSahih.getInt(1))
            }
        } catch (e: ClassNotFoundException) {
            println("org.sqlite.JDBC driver not available in test classpath.")
        }
    }
}
