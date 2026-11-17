package com.poswal.noor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.poswal.noor.data.local.dao.ContentDao
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.local.entity.SurahEntity

@Database(
    entities = [
        ContentItemEntity::class,
        SurahEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class NoorDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao

    companion object {
        const val DATABASE_NAME = "poswal_noor.db"
    }
}
