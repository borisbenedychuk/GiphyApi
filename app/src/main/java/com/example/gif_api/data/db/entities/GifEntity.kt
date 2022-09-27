package com.example.gif_api.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.gif_api.domain.model.gif.GifModel

@Entity(tableName = "gif", primaryKeys = ["id", "query"])
data class GifEntity(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "page")
    val page: Int,
    @ColumnInfo(name = "original_url")
    val originalUrl: String,
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
    )
}

