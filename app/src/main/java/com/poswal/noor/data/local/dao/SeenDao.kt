package com.poswal.noor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.poswal.noor.data.local.entity.SeenItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: SeenItemEntity)

    @Query("SELECT item_id FROM seen_items ORDER BY shown_at DESC LIMIT :limit")
    suspend fun getRecentlySeenIds(limit: Int): List<Long>

    @Query("SELECT item_id FROM seen_items")
    suspend fun getAllSeenIds(): List<Long>

    @Query("SELECT item_id FROM seen_items ORDER BY shown_at ASC LIMIT :limit")
    suspend fun getLeastRecentlySeenIds(limit: Int): List<Long>

    @Query("SELECT COUNT(*) FROM seen_items")
    suspend fun getSeenCount(): Int

    @Query("SELECT * FROM seen_items WHERE item_id = :itemId LIMIT 1")
    suspend fun getSeenItem(itemId: Long): SeenItemEntity?

    @Query("DELETE FROM seen_items WHERE item_id IN (SELECT item_id FROM seen_items ORDER BY shown_at ASC LIMIT :count)")
    suspend fun pruneOldSeen(count: Int)
}
