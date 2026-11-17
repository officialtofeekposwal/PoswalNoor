package com.poswal.noor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.poswal.noor.data.local.entity.UserCollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCollectionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(collection: UserCollectionEntity): Long

    @Query("SELECT * FROM user_collections ORDER BY name ASC")
    fun getAllCollections(): Flow<List<UserCollectionEntity>>

    @Query("SELECT name FROM user_collections ORDER BY name ASC")
    fun getAllCollectionNames(): Flow<List<String>>

    @Query("SELECT * FROM user_collections WHERE name = :name LIMIT 1")
    suspend fun getCollectionByName(name: String): UserCollectionEntity?

    @Query("DELETE FROM user_collections WHERE name = :name")
    suspend fun deleteByName(name: String)

    @Query("SELECT COUNT(*) FROM user_collections")
    suspend fun getCollectionCount(): Int
}
