package com.poswal.noor.domain.repository

import com.poswal.noor.data.local.entity.ContentItemEntity
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    fun getContentById(id: Long): Flow<ContentItemEntity?>
    suspend fun getContentByIdSync(id: Long): ContentItemEntity?
    suspend fun getRandomWidgetContent(
        types: List<String>,
        topics: List<String>,
        maxWordCount: Int = 40
    ): ContentItemEntity?
    fun getFilteredContent(
        types: List<String>,
        topics: List<String>
    ): Flow<List<ContentItemEntity>>
    fun getFavorites(): Flow<List<ContentItemEntity>>
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
}
