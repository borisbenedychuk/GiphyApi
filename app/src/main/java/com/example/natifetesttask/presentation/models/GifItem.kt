package com.example.natifetesttask.presentation.models

import com.example.natifetesttask.domain.model.GifModel

data class GifItem(
    val id: String,
    val title: String,
    val originalUrl: String,
    val smallUrl: String,
    val previewUrl: String,
)

fun GifModel.asItem() = GifItem(
    id = id,
    title = title,
    originalUrl = originalUrl,
    smallUrl = smallUrl,
    previewUrl = previewUrl,
)
