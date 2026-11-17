package com.poswal.noor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.poswal.noor.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE content_id = :contentId")
    suspend fun deleteByContentId(contentId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE content_id = :contentId)")
    fun isBookmarked(contentId: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE content_id = :contentId)")
    suspend fun isBookmarkedSync(contentId: Long): Boolean

    @Query("SELECT * FROM bookmarks ORDER BY created_at DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("UPDATE bookmarks SET note = :note WHERE content_id = :contentId")
    suspend fun updateNote(contentId: Long, note: String?)

    @Query("SELECT DISTINCT folder FROM bookmarks")
    fun getAllFolders(): Flow<List<String>>

    @Query("SELECT content_id FROM bookmarks")
    fun getAllBookmarkedContentIds(): Flow<List<Long>>

    @Query("SELECT COUNT(*) FROM bookmarks")
    suspend fun getBookmarkCount(): Int
}
