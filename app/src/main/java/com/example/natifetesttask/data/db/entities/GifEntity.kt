package com.example.natifetesttask.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.natifetesttask.domain.model.GifModel

@Entity(tableName = "gif")
data class GifEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "page")
    val page: Int,
    @ColumnInfo(name = "gif_url")
    val gifUrl: String,
    @ColumnInfo(name = "preview_url")
    val previewUrl: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "query")
    val query: String,
) {
    fun asGifModel() = GifModel(
        id = id,
        title = title,
        gifUrl = gifUrl,
        previewUrl = previewUrl,
    )
}

