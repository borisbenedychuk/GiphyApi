package com.example.natifetesttask.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.natifetesttask.domain.model.GifModel

@Entity(tableName = "gif", primaryKeys = ["id", "query"])
data class GifEntity(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "page")
    val page: Int,
    @ColumnInfo(name = "original_url")
    val originalUrl: String,
    @ColumnInfo(name = "preview_url")
    val previewUrl: String,
    @ColumnInfo(name = "small_url")
    val smallUrl: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "query")
    val query: String,
) {
    fun asGifModel() = GifModel(
        id = id,
        title = title,
        originalUrl = originalUrl,
        smallUrl = smallUrl,
        previewUrl = previewUrl,
    )
}

