package com.poswal.noor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.poswal.noor.data.local.entity.ContentItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {

    @Query("SELECT * FROM content_items WHERE id = :id LIMIT 1")
    fun getContentById(id: Long): Flow<ContentItemEntity?>

    @Query("SELECT * FROM content_items WHERE id = :id LIMIT 1")
    suspend fun getContentByIdSync(id: Long): ContentItemEntity?

    @Query(
        """
        SELECT * FROM content_items 
        WHERE (:typesCount = 0 OR type IN (:types))
        AND (:topicsCount = 0 OR topic IN (:topics))
        ORDER BY id ASC
        """
    )
    fun getFilteredContent(
        types: List<String>,
        typesCount: Int,
        topics: List<String>,
        topicsCount: Int
    ): Flow<List<ContentItemEntity>>

    @Query(
        """
        SELECT * FROM content_items 
        WHERE (:typesCount = 0 OR type IN (:types))
        AND (:topicsCount = 0 OR topic IN (:topics))
        AND word_count <= :maxWordCount
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

    @Query("SELECT * FROM content_items WHERE is_favorite = 1 ORDER BY id DESC")
    fun getFavorites(): Flow<List<ContentItemEntity>>

    @Query("UPDATE content_items SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ContentItemEntity>)

    @Query("SELECT COUNT(*) FROM content_items")
    suspend fun getCount(): Int
}
