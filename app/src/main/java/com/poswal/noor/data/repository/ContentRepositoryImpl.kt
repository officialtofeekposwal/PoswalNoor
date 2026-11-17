package com.poswal.noor.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.poswal.noor.data.local.dao.BookmarkDao
import com.poswal.noor.data.local.dao.ContentDao
import com.poswal.noor.data.local.dao.SeenDao
import com.poswal.noor.data.local.entity.BookmarkEntity
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.local.entity.SeenItemEntity
import com.poswal.noor.data.local.entity.SurahEntity
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl @Inject constructor(
    private val contentDao: ContentDao,
    private val seenDao: SeenDao,
    private val bookmarkDao: BookmarkDao
) : ContentRepository {

    override fun getContentById(id: Long): Flow<ContentItemEntity?> {
        return contentDao.getContentById(id)
    }

    override suspend fun getContentByIdSync(id: Long): ContentItemEntity? {
        return contentDao.getContentByIdSync(id)
    }

    // --- Paging 3 Infinite Feed with No-Repeat Engine ---
    override fun getPagedFeed(
        contentType: ContentType,
        topicKey: String?,
        seed: Long
    ): Flow<PagingData<ContentItemEntity>> {
        val types = when (contentType) {
            ContentType.BOTH -> listOf("quran", "hadith")
            ContentType.QURAN -> listOf("quran")
            ContentType.HADITH -> listOf("hadith")
        }
        val topics = topicKey?.let { listOf(it) } ?: emptyList()

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NoRepeatPagingSource(
                    contentDao = contentDao,
                    seenDao = seenDao,
                    types = types,
                    topics = topics,
                    seed = seed,
                    noRepeatWindowSize = 200
                )
            }
        ).flow
    }

    // --- No-Repeat Widget Engine ---
    override suspend fun getNoRepeatItemForWidget(
        types: List<String>,
        topics: List<String>,
        maxWordCount: Int
    ): ContentItemEntity? {
        val recentlySeen = seenDao.getRecentlySeenIds(200)

        // Try getting an unseen item first
        var item = contentDao.getRandomWidgetContentExcluding(
            types = types,
            typesCount = types.size,
            topics = topics,
            topicsCount = topics.size,
            maxWordCount = maxWordCount,
            excludeIds = recentlySeen
        )

        // If all items in this filter have been seen, fall back to least-recently-seen
        if (item == null) {
            item = contentDao.getRandomWidgetContent(
                types = types,
                typesCount = types.size,
                topics = topics,
                topicsCount = topics.size,
                maxWordCount = maxWordCount
            )
        }

        if (item != null) {
            markItemSeen(item.id, item.type)
        }
        return item
    }

    override suspend fun getVerseOfTheDay(epochDay: Long): ContentItemEntity? {
        // Deterministic daily seed: same day produces same item, next day produces new item
        val dailySeed = (epochDay * 2654435761L) and 0x7FFFFFFF
        val items = contentDao.getDeterministicPage(
            types = listOf("quran"),
            typesCount = 1,
            topics = emptyList(),
            topicsCount = 0,
            excludeIds = emptyList(),
            excludeCount = 0,
            seed = dailySeed,
            limit = 1,
            offset = 0
        )
        return items.firstOrNull()
    }

    override suspend fun markItemSeen(itemId: Long, type: String) {
        val existing = seenDao.getSeenItem(itemId)
        val count = (existing?.shownCount ?: 0) + 1
        seenDao.insertOrUpdate(
            SeenItemEntity(
                itemId = itemId,
                type = type,
                shownAt = System.currentTimeMillis(),
                shownCount = count
            )
        )
    }

    // --- Legacy / Compatibility ---
    override suspend fun getRandomWidgetContent(
        types: List<String>,
        topics: List<String>,
        maxWordCount: Int
    ): ContentItemEntity? {
        return getNoRepeatItemForWidget(types, topics, maxWordCount)
    }

    override fun getFilteredContent(
        types: List<String>,
        topics: List<String>
    ): Flow<List<ContentItemEntity>> {
        return emptyFlow()
    }

    override fun getFavorites(): Flow<List<ContentItemEntity>> {
        return emptyFlow()
    }

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        toggleBookmark(id)
    }

    // --- Bookmarks (User DB) ---
    override fun isBookmarked(contentId: Long): Flow<Boolean> {
        return bookmarkDao.isBookmarked(contentId)
    }

    override suspend fun toggleBookmark(contentId: Long, folder: String, note: String?) {
        val exists = bookmarkDao.isBookmarkedSync(contentId)
        if (exists) {
            bookmarkDao.deleteByContentId(contentId)
        } else {
            bookmarkDao.insert(
                BookmarkEntity(
                    contentId = contentId,
                    folder = folder,
                    note = note
                )
            )
        }
    }

    override fun getAllBookmarks(): Flow<List<BookmarkEntity>> {
        return bookmarkDao.getAllBookmarks()
    }

    override suspend fun updateBookmarkNote(contentId: Long, note: String?) {
        bookmarkDao.updateNote(contentId, note)
    }

    override fun getAllBookmarkFolders(): Flow<List<String>> {
        return bookmarkDao.getAllFolders()
    }

    // --- Full Quran & Hadith Browsing ---
    override fun getAllSurahs(): Flow<List<SurahEntity>> {
        return contentDao.getAllSurahs()
    }

    override fun getAyahsForSurah(surahNumber: Int): Flow<List<ContentItemEntity>> {
        return contentDao.getAyahsForSurah(surahNumber)
    }

    override suspend fun getSurahByNumber(number: Int): SurahEntity? {
        return contentDao.getSurahByNumber(number)
    }

    override fun getPagedHadiths(collection: String): Flow<PagingData<ContentItemEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { contentDao.getHadithsForBook(collection) }
        ).flow
    }

    // --- Offline Search ---
    override fun searchContent(query: String): Flow<List<ContentItemEntity>> {
        return contentDao.searchContent(query)
    }
}
