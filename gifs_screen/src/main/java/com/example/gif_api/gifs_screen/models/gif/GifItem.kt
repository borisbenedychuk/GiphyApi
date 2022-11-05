package com.example.gif_api.gifs_screen.models.gif

import com.example.gif_api.domain.gif.model.GifModel

data class GifItem(
    val id: String,
    val title: String,
    val originalUrl: String,
    val smallUrl: String,
)

fun GifModel.asItem() = GifItem(
    id = id,
    title = title,
    originalUrl = originalUrl,
    smallUrl = smallUrl,
)
