package com.poswal.noor.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "seen_items",
    indices = [
        Index(value = ["shown_at"]),
        Index(value = ["type"])
    ]
)
data class SeenItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "item_id")
    val itemId: Long,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "shown_at")
    val shownAt: Long,
    @ColumnInfo(name = "shown_count")
    val shownCount: Int = 1
)
