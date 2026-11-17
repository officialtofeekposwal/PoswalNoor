package com.poswal.noor.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.local.entity.SurahEntity
import com.poswal.noor.domain.model.HadithBookInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {

    @Query("SELECT * FROM content_items WHERE id = :id LIMIT 1")
    fun getContentById(id: Long): Flow<ContentItemEntity?>

    @Query("SELECT * FROM content_items WHERE id = :id LIMIT 1")
    suspend fun getContentByIdSync(id: Long): ContentItemEntity?

    @Query("SELECT * FROM content_items WHERE id IN (:ids)")
    suspend fun getContentItemsByIds(ids: List<Long>): List<ContentItemEntity>

    @Query(
        """
        SELECT * FROM content_items 
        WHERE (:typesCount = 0 OR type IN (:types))
        AND (:topicsCount = 0 OR topic IN (:topics))
        ORDER BY id ASC
        """
    )
    fun getPagingContent(
        types: List<String>,
        typesCount: Int,
        topics: List<String>,
        topicsCount: Int
    ): PagingSource<Int, ContentItemEntity>

    @Query(
        """
        SELECT * FROM content_items 
        WHERE (:typesCount = 0 OR type IN (:types))
        AND (:topicsCount = 0 OR topic IN (:topics))
        AND word_count_en <= :maxWordCount
        ORDER BY RANDOM() 
        LIMIT 1
        """
    )
    suspend fun getRandomWidgetContent(
        types: List<String>,
        typesCount: Int,
        topics: List<String>,
        topicsCount: Int,
        maxWordCount: Int = 40
    ): ContentItemEntity?

    @Query(
        """
        SELECT * FROM content_items 
        WHERE (:typesCount = 0 OR type IN (:types))
        AND (:topicsCount = 0 OR topic IN (:topics))
        AND word_count_en <= :maxWordCount
        AND id NOT IN (:excludeIds)
        ORDER BY RANDOM()
        LIMIT 1
        """
    )
    suspend fun getRandomWidgetContentExcluding(
        types: List<String>,
        typesCount: Int,
        topics: List<String>,
        topicsCount: Int,
        maxWordCount: Int,
        excludeIds: List<Long>
    ): ContentItemEntity?

    @Query(
        """
        SELECT * FROM content_items 
        WHERE (:typesCount = 0 OR type IN (:types))
        AND (:topicsCount = 0 OR topic IN (:topics))
        ORDER BY RANDOM()
        LIMIT :limit
        """
    )
    suspend fun getRandomItems(
        types: List<String>,
        typesCount: Int,
        topics: List<String>,
        topicsCount: Int,
        limit: Int
    ): List<ContentItemEntity>

    @Query(
        """
        SELECT * FROM content_items 
        WHERE (:typesCount = 0 OR type IN (:types))
        AND (:topicsCount = 0 OR topic IN (:topics))
        ORDER BY 
            CASE WHEN (:excludeCount > 0 AND id IN (:excludeIds)) THEN 1 ELSE 0 END ASC,
            (((id * (((:seed * 1103515245 + 12345) & 2147483647) | 1)) + 12345) & 2147483647) ASC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getDeterministicPage(
        types: List<String>,
        typesCount: Int,
        topics: List<String>,
        topicsCount: Int,
        excludeIds: List<Long>,
        excludeCount: Int,
        seed: Long,
        limit: Int,
        offset: Int
    ): List<ContentItemEntity>

    @Query(
        """
        SELECT COUNT(*) FROM content_items 
        WHERE (:typesCount = 0 OR type IN (:types))
        AND (:topicsCount = 0 OR topic IN (:topics))
        """
    )
    suspend fun getFilteredCount(
        types: List<String>,
        typesCount: Int,
        topics: List<String>,
        topicsCount: Int
    ): Int

    // --- Surahs & Full Quran Reader ---
    @Query("SELECT * FROM surahs ORDER BY number ASC")
    fun getAllSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE number = :number LIMIT 1")
    suspend fun getSurahByNumber(number: Int): SurahEntity?

    @Query("SELECT * FROM content_items WHERE type = 'quran' AND surah = :surahNumber ORDER BY ayah ASC")
    fun getAyahsForSurah(surahNumber: Int): Flow<List<ContentItemEntity>>

    @Query("SELECT * FROM content_items WHERE type = 'quran' AND surah = :surah AND ayah = :ayah LIMIT 1")
    suspend fun getSurahAyah(surah: Int, ayah: Int): ContentItemEntity?

    // --- Hadith Navigation ---
    @Query("SELECT DISTINCT book FROM content_items WHERE type = 'hadith' AND book IS NOT NULL ORDER BY book ASC")
    fun getHadithCollections(): Flow<List<String>>

    @Query("""
        SELECT surah, surah_name, COUNT(*) AS hadith_count 
        FROM content_items 
        WHERE type = 'hadith' AND book = :collection AND surah IS NOT NULL
        GROUP BY surah, surah_name 
        ORDER BY surah ASC
    """)
    fun getHadithBooks(collection: String): Flow<List<HadithBookInfo>>

    @Query("""
        SELECT * FROM content_items 
        WHERE type = 'hadith' AND book = :collection AND surah = :bookNumber 
        ORDER BY CAST(number AS INTEGER) ASC, id ASC
    """)
    fun getPagedHadithsForBook(collection: String, bookNumber: Int): PagingSource<Int, ContentItemEntity>

    @Query("""
        SELECT * FROM content_items 
        WHERE type = 'hadith' AND book = :collection 
        ORDER BY CAST(number AS INTEGER) ASC, id ASC
    """)
    fun getPagedHadithsForCollection(collection: String): PagingSource<Int, ContentItemEntity>

    @Query("SELECT * FROM content_items WHERE type = 'hadith' AND book = :book ORDER BY CAST(number AS INTEGER) ASC")
    fun getHadithsForBook(book: String): PagingSource<Int, ContentItemEntity>

    // --- Offline Search ---
    @Query(
        """
        SELECT * FROM content_items 
        WHERE arabic LIKE '%' || :query || '%' 
        OR translation_en LIKE '%' || :query || '%'
        OR translation_hi LIKE '%' || :query || '%'
        OR translation_ur LIKE '%' || :query || '%'
        OR reference LIKE '%' || :query || '%'
        ORDER BY id ASC
        LIMIT 100
        """
    )
    fun searchContent(query: String): Flow<List<ContentItemEntity>>

    // --- Metadata / Counts ---
    @Query("SELECT COUNT(*) FROM content_items")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM content_items WHERE type = 'quran'")
    suspend fun getQuranCount(): Int

    @Query("SELECT COUNT(*) FROM content_items WHERE type = 'hadith'")
    suspend fun getHadithCount(): Int
}
