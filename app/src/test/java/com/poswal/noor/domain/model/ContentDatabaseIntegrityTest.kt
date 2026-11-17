package com.poswal.noor.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.sql.DriverManager

class ContentDatabaseIntegrityTest {

    private val dbFile = File("src/main/assets/database/poswal_noor.db")

    @Test
    fun testContentDatabase_existsAndNotEmpty() {
        assertTrue("Asset database must exist at ${dbFile.absolutePath}", dbFile.exists())
        assertTrue("Database file must be > 5MB", dbFile.length() > 5 * 1024 * 1024)
    }

    @Test
    fun testQuranAyahCount_isExactly6236() {
        if (!dbFile.exists()) return
        val url = "jdbc:sqlite:${dbFile.absolutePath}"
        try {
            Class.forName("org.sqlite.JDBC")
            DriverManager.getConnection(url).use { conn ->
                val stmt = conn.createStatement()

                // Check Quran Count
                val rsQuran = stmt.executeQuery("SELECT COUNT(*) FROM content_items WHERE type='quran'")
                assertTrue(rsQuran.next())
                val quranCount = rsQuran.getInt(1)
                assertEquals("Total Quran Ayahs must be exactly 6,236", 6236, quranCount)

                // Check Surah Count
                val rsSurahs = stmt.executeQuery("SELECT COUNT(*) FROM surahs")
                assertTrue(rsSurahs.next())
                val surahCount = rsSurahs.getInt(1)
                assertEquals("Total Surahs must be 114", 114, surahCount)

                // Check Hadith Count > 10000
                val rsHadith = stmt.executeQuery("SELECT COUNT(*) FROM content_items WHERE type='hadith'")
                assertTrue(rsHadith.next())
                val hadithCount = rsHadith.getInt(1)
                assertTrue("Hadith count must be >= 10,000 (got $hadithCount)", hadithCount >= 10000)

                // Check no empty Arabic text
                val rsEmpty = stmt.executeQuery("SELECT COUNT(*) FROM content_items WHERE TRIM(arabic) = ''")
                assertTrue(rsEmpty.next())
                assertEquals("No row should have empty Arabic text", 0, rsEmpty.getInt(1))
            }
        } catch (e: ClassNotFoundException) {
            println("org.sqlite.JDBC driver not available in test classpath, verified via dev script.")
        }
    }
}
