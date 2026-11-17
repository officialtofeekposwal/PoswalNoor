package com.poswal.noor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.poswal.noor.data.local.dao.BookmarkDao
import com.poswal.noor.data.local.dao.SeenDao
import com.poswal.noor.data.local.dao.UserCollectionDao
import com.poswal.noor.data.local.entity.BookmarkEntity
import com.poswal.noor.data.local.entity.SeenItemEntity
import com.poswal.noor.data.local.entity.UserCollectionEntity

@Database(
    entities = [
        SeenItemEntity::class,
        BookmarkEntity::class,
        UserCollectionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun seenDao(): SeenDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun userCollectionDao(): UserCollectionDao

    companion object {
        const val DATABASE_NAME = "poswal_user.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_collections` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `created_at` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_user_collections_name` ON `user_collections` (`name`)"
                )
            }
        }
    }
}
