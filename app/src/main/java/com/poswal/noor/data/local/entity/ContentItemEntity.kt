package com.poswal.noor.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "content_items",
    indices = [
        Index(value = ["type", "topic", "word_count"]),
        Index(value = ["book", "number", "type"], unique = true)
    ]
)
data class ContentItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "type") val type: String, // "quran" | "hadith"
    @ColumnInfo(name = "arabic") val arabic: String,
    @ColumnInfo(name = "translation_en") val translationEn: String,
    @ColumnInfo(name = "translation_hinglish") val translationHinglish: String? = null,
    @ColumnInfo(name = "translation_hi") val translationHi: String? = null,
    @ColumnInfo(name = "translation_ur") val translationUr: String? = null,
    @ColumnInfo(name = "reference") val reference: String,
    @ColumnInfo(name = "book") val book: String,
    @ColumnInfo(name = "number") val number: Int,
    @ColumnInfo(name = "grade") val grade: String?,
    @ColumnInfo(name = "topic") val topic: String,
    @ColumnInfo(name = "word_count") val wordCount: Int,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false
)
