package com.poswal.noor.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.poswal.noor.data.local.dao.ContentDao
import com.poswal.noor.data.local.dao.SeenDao
import com.poswal.noor.data.local.entity.ContentItemEntity

/**
 * Paging 3 PagingSource that guarantees:
 * 1. Unseen items are prioritized over recently seen items.
 * 2. Deterministic session shuffle (stable on device rotation, refreshed on pull-to-refresh).
 * 3. Zero-duplication within the configurable window (e.g. 200 items).
 * 4. DB-level filtering and pagination without loading the entire DB into RAM.
 */
class NoRepeatPagingSource(
    private val contentDao: ContentDao,
    private val seenDao: SeenDao,
    private val types: List<String>,
    private val topics: List<String>,
    private val seed: Long,
    private val noRepeatWindowSize: Int = 200
) : PagingSource<Int, ContentItemEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentItemEntity> {
        val page = params.key ?: 0
        val pageSize = params.loadSize
        val offset = page * pageSize

        return try {
            val recentlySeen = seenDao.getRecentlySeenIds(noRepeatWindowSize)

            val items = contentDao.getDeterministicPage(
                types = types,
                typesCount = types.size,
                topics = topics,
                topicsCount = topics.size,
                excludeIds = recentlySeen,
                excludeCount = recentlySeen.size,
                seed = seed,
                limit = pageSize,
                offset = offset
            )

            val totalCount = contentDao.getFilteredCount(
                types = types,
                typesCount = types.size,
                topics = topics,
                topicsCount = topics.size
            )

            val nextKey = if (offset + items.size >= totalCount || items.isEmpty()) null else page + 1
            val prevKey = if (page == 0) null else page - 1

            LoadResult.Page(
                data = items,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ContentItemEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
