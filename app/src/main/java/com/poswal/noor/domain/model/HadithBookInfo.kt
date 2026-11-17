package com.poswal.noor.domain.model

import androidx.room.ColumnInfo

data class HadithBookInfo(
    @ColumnInfo(name = "surah") val bookNumber: Int,
    @ColumnInfo(name = "surah_name") val bookName: String,
    @ColumnInfo(name = "hadith_count") val hadithCount: Int
)
