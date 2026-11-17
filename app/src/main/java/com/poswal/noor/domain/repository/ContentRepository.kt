package com.poswal.noor.domain.repository

import androidx.paging.PagingData
import com.poswal.noor.data.local.entity.BookmarkEntity
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.local.entity.SurahEntity
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.model.HadithBookInfo
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    fun getContentById(id: Long): Flow<ContentItemEntity?>
    suspend fun getContentByIdSync(id: Long): ContentItemEntity?

    // --- Paging 3 Infinite Feed ---
    fun getPagedFeed(
        contentType: ContentType,
        topicKey: String?,
        seed: Long
    ): Flow<PagingData<ContentItemEntity>>

    // --- No-Repeat Engine for Widgets & Daily Features ---
    suspend fun getNoRepeatItemForWidget(
        types: List<String>,
        topics: List<String>,
        maxWordCount: Int
    ): ContentItemEntity?

    suspend fun getVerseOfTheDay(epochDay: Long): ContentItemEntity?

    suspend fun markItemSeen(itemId: Long, type: String)

    // --- Legacy / Compatibility ---
    suspend fun getRandomWidgetContent(
        types: List<String>,
        topics: List<String>,
        maxWordCount: Int = 40
    ): ContentItemEntity?

    fun getFilteredContent(
        types: List<String>,
        topics: List<String>
    ): Flow<List<ContentItemEntity>>

    fun getFavorites(): Flow<List<ContentItemEntity>>
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)

    // --- Bookmarks (User DB) ---
    fun isBookmarked(contentId: Long): Flow<Boolean>
    suspend fun toggleBookmark(contentId: Long, folder: String = "Default", note: String? = null)
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>
    suspend fun updateBookmarkNote(contentId: Long, note: String?)
    fun getAllBookmarkFolders(): Flow<List<String>>

    // --- Full Quran & Hadith Browsing ---
    fun getAllSurahs(): Flow<List<SurahEntity>>
    fun getAyahsForSurah(surahNumber: Int): Flow<List<ContentItemEntity>>
    suspend fun getSurahByNumber(number: Int): SurahEntity?
    fun getHadithBooks(collection: String): Flow<List<HadithBookInfo>>
    fun getPagedHadiths(collection: String, bookNumber: Int? = null): Flow<PagingData<ContentItemEntity>>

    // --- Offline Search ---
    fun searchContent(query: String): Flow<List<ContentItemEntity>>
}
