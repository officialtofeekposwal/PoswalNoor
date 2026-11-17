package com.poswal.noor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.poswal.noor.data.local.dao.BookmarkDao
import com.poswal.noor.data.local.dao.SeenDao
import com.poswal.noor.data.local.entity.BookmarkEntity
import com.poswal.noor.data.local.entity.SeenItemEntity

@Database(
    entities = [
        SeenItemEntity::class,
        BookmarkEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun seenDao(): SeenDao
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        const val DATABASE_NAME = "poswal_user.db"
    }
}
