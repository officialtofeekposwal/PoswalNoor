package com.poswal.noor.feature.saved

import com.poswal.noor.data.local.entity.BookmarkEntity
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.model.SavedItem
import com.poswal.noor.feature.saved.model.SavedTypeFilter
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedPhase5Test {

    @Test
    fun testSavedTypeFilter_mapping() {
        assertEquals(ContentType.BOTH, SavedTypeFilter.ALL.contentType)
        assertEquals(ContentType.QURAN, SavedTypeFilter.AYAHS.contentType)
        assertEquals(ContentType.HADITH, SavedTypeFilter.HADITH.contentType)
    }

    @Test
    fun testJsonExportFormat_structure() {
        val root = JSONObject()
        root.put("app", "Poswal Noor")
        root.put("version", 1)
        root.put("exported_at", 1731855600000L)

        val collectionsArray = JSONArray()
        collectionsArray.put("Morning Duas")
        collectionsArray.put("Sabr & Hope")
        root.put("collections", collectionsArray)

        val bookmarksArray = JSONArray()
        val b1 = JSONObject().apply {
            put("content_id", 160L)
            put("folder", "Sabr & Hope")
            put("note", "Seek help through patience and prayer.")
            put("created_at", 1731855600000L)
        }
        bookmarksArray.put(b1)
        root.put("bookmarks", bookmarksArray)

        val jsonString = root.toString(2)

        // Parse back and verify
        val parsed = JSONObject(jsonString)
        assertEquals("Poswal Noor", parsed.getString("app"))
        assertEquals(1, parsed.getInt("version"))
        assertEquals(2, parsed.getJSONArray("collections").length())
        assertEquals("Morning Duas", parsed.getJSONArray("collections").getString(0))

        val parsedBookmarks = parsed.getJSONArray("bookmarks")
        assertEquals(1, parsedBookmarks.length())
        val parsedB1 = parsedBookmarks.getJSONObject(0)
        assertEquals(160L, parsedB1.getLong("content_id"))
        assertEquals("Sabr & Hope", parsedB1.getString("folder"))
        assertEquals("Seek help through patience and prayer.", parsedB1.getString("note"))
    }

    @Test
    fun testJsonImport_parsingAndResilience() {
        val sampleJson = """
            {
              "app": "Poswal Noor",
              "version": 1,
              "exported_at": 1731855600000,
              "collections": ["Daily Inspiration", "", "   ", "Gratitude"],
              "bookmarks": [
                {
                  "content_id": 1,
                  "folder": "Daily Inspiration",
                  "note": "Al-Fatihah",
                  "created_at": 1731855600000
                },
                {
                  "content_id": 2,
                  "folder": "Gratitude",
                  "note": null
                },
                {
                  "content_id": -5,
                  "folder": "Invalid"
                }
              ]
            }
        """.trimIndent()

        val root = JSONObject(sampleJson)

        // Parse collections (ignoring blank)
        val validCollections = mutableListOf<String>()
        val collectionsArr = root.getJSONArray("collections")
        for (i in 0 until collectionsArr.length()) {
            val name = collectionsArr.getString(i).trim()
            if (name.isNotBlank()) {
                validCollections.add(name)
            }
        }
        assertEquals(listOf("Daily Inspiration", "Gratitude"), validCollections)

        // Parse bookmarks (ignoring invalid content_id <= 0)
        val validBookmarks = mutableListOf<BookmarkEntity>()
        val bookmarksArr = root.getJSONArray("bookmarks")
        for (i in 0 until bookmarksArr.length()) {
            val bObj = bookmarksArr.getJSONObject(i)
            val contentId = bObj.optLong("content_id", -1L)
            if (contentId > 0) {
                val folder = bObj.optString("folder", "Default").ifBlank { "Default" }
                val note = if (bObj.has("note") && !bObj.isNull("note")) bObj.getString("note") else null
                validBookmarks.add(
                    BookmarkEntity(
                        contentId = contentId,
                        folder = folder,
                        note = note
                    )
                )
            }
        }
        assertEquals(2, validBookmarks.size)
        assertEquals(1L, validBookmarks[0].contentId)
        assertEquals("Al-Fatihah", validBookmarks[0].note)
        assertEquals(2L, validBookmarks[1].contentId)
        assertEquals(null, validBookmarks[1].note)
    }

    @Test
    fun testSavedItems_searchAndFilterLogic() {
        val item1 = SavedItem(
            bookmark = BookmarkEntity(id = 1, contentId = 100, folder = "Patience", note = "My daily reminder to stay calm"),
            content = ContentItemEntity(
                id = 100,
                type = "quran",
                reference = "Al-Baqarah 2:153",
                arabic = "يَا أَيُّهَا الَّذِينَ آمَنُوا اسْتَعِينُوا بِالصَّبْرِ وَالصَّلَاةِ",
                translationEn = "O you who have believed, seek help through patience and prayer.",
                surah = 2,
                ayah = 153,
                wordCount = 11
            )
        )

        val item2 = SavedItem(
            bookmark = BookmarkEntity(id = 2, contentId = 200, folder = "Default", note = null),
            content = ContentItemEntity(
                id = 200,
                type = "hadith",
                reference = "Sahih al-Bukhari 1",
                arabic = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ",
                translationEn = "Actions are according to intentions.",
                surah = 1,
                number = "1",
                wordCount = 5
            )
        )

        val items = listOf(item1, item2)

        // 1. Search by note text
        fun search(query: String) = items.filter { item ->
            item.content.reference.contains(query, ignoreCase = true) ||
                    item.content.translationEn.contains(query, ignoreCase = true) ||
                    item.content.arabic.contains(query, ignoreCase = true) ||
                    (item.bookmark.note?.contains(query, ignoreCase = true) == true)
        }

        val noteResults = search("stay calm")
        assertEquals(1, noteResults.size)
        assertEquals(100L, noteResults.first().content.id)

        // 2. Search by reference
        val refResults = search("Bukhari")
        assertEquals(1, refResults.size)
        assertEquals(200L, refResults.first().content.id)

        // 3. Search by Arabic text
        val arabicResults = search("النِّيَّاتِ")
        assertEquals(1, arabicResults.size)
        assertEquals(200L, arabicResults.first().content.id)

        // 4. Notes-only filter
        val notesOnlyItems = items.filter { !it.bookmark.note.isNullOrBlank() }
        assertEquals(1, notesOnlyItems.size)
        assertEquals(100L, notesOnlyItems.first().content.id)
    }
}
