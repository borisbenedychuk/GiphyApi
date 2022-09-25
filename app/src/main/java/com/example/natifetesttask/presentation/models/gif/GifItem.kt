package com.example.natifetesttask.presentation.models.gif

import com.example.natifetesttask.domain.model.gif.GifModel

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
