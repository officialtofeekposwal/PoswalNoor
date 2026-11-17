package com.poswal.noor.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "content_items",
    indices = [
        Index(value = ["type"]),
        Index(value = ["topic"]),
        Index(value = ["word_count_en"]),
        Index(value = ["surah", "ayah"]),
        Index(value = ["book", "number"])
    ]
)
data class ContentItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "type") val type: String, // "quran" | "hadith"
    @ColumnInfo(name = "surah") val surah: Int? = null,
    @ColumnInfo(name = "ayah") val ayah: Int? = null,
    @ColumnInfo(name = "surah_name") val surahName: String? = null,
    @ColumnInfo(name = "arabic") val arabic: String,
    @ColumnInfo(name = "translation_en") val translationEn: String,
    @ColumnInfo(name = "translation_hinglish") val translationHinglish: String? = null,
    @ColumnInfo(name = "translation_hi") val translationHi: String? = null,
    @ColumnInfo(name = "translation_ur") val translationUr: String? = null,
    @ColumnInfo(name = "reference") val reference: String,
    @ColumnInfo(name = "book") val book: String? = null,
    @ColumnInfo(name = "number") val number: String? = null,
    @ColumnInfo(name = "grade") val grade: String? = null,
    @ColumnInfo(name = "narrator") val narrator: String? = null,
    @ColumnInfo(name = "topic") val topic: String? = null,
    @ColumnInfo(name = "word_count") val wordCount: Int,
    @ColumnInfo(name = "word_count_en") val wordCountEn: Int = wordCount,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false
)
