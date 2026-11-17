package com.poswal.noor.domain.model

import com.poswal.noor.data.local.entity.BookmarkEntity
import com.poswal.noor.data.local.entity.ContentItemEntity

/**
 * Composite model representing a user bookmark joined with its verified content.
 */
data class SavedItem(
    val bookmark: BookmarkEntity,
    val content: ContentItemEntity
)
