package com.poswal.noor.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "surahs",
    indices = [
        Index(value = ["number"], unique = true)
    ]
)
data class SurahEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "number") val number: Int,
    @ColumnInfo(name = "name_arabic") val nameArabic: String,
    @ColumnInfo(name = "name_english") val nameEnglish: String,
    @ColumnInfo(name = "name_translation") val nameTranslation: String,
    @ColumnInfo(name = "total_verses") val totalVerses: Int,
    @ColumnInfo(name = "revelation_type") val revelationType: String
)
