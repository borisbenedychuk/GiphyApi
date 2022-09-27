package com.example.gif_api.presentation.models.gif

import com.example.gif_api.domain.model.gif.GifModel

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
