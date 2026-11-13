package com.poswal.noor.di

import android.content.Context
import androidx.room.Room
import com.poswal.noor.data.local.NoorDatabase
import com.poswal.noor.data.local.dao.ContentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNoorDatabase(
        @ApplicationContext context: Context
    ): NoorDatabase {
        return Room.databaseBuilder(
            context,
            NoorDatabase::class.java,
            NoorDatabase.DATABASE_NAME
        )
            .createFromAsset("database/poswal_noor.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideContentDao(database: NoorDatabase): ContentDao {
        return database.contentDao()
    }
}
