package com.poswal.noor.data.repository

import com.poswal.noor.data.local.dao.ContentDao
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl @Inject constructor(
    private val contentDao: ContentDao
) : ContentRepository {

    override fun getContentById(id: Long): Flow<ContentItemEntity?> =
        contentDao.getContentById(id)

    override suspend fun getContentByIdSync(id: Long): ContentItemEntity? =
        contentDao.getContentByIdSync(id)

    override suspend fun getRandomWidgetContent(
        types: List<String>,
        topics: List<String>,
        maxWordCount: Int
    ): ContentItemEntity? {
        return contentDao.getRandomWidgetContent(
            types = types,
            typesCount = types.size,
            topics = topics,
            topicsCount = topics.size,
            maxWordCount = maxWordCount
        )
    }

    override fun getFilteredContent(
        types: List<String>,
        topics: List<String>
    ): Flow<List<ContentItemEntity>> {
        return contentDao.getFilteredContent(
            types = types,
            typesCount = types.size,
            topics = topics,
            topicsCount = topics.size
        )
    }

    override fun getFavorites(): Flow<List<ContentItemEntity>> =
        contentDao.getFavorites()

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) =
        contentDao.setFavorite(id, isFavorite)
}
