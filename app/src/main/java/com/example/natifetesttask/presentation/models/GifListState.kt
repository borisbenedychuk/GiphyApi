package com.example.natifetesttask.presentation.models

import com.example.natifetesttask.domain.model.GifModel

data class ScreenState<T>(
    val data: T? = null,
    val loading: Boolean = false,
    val errors: List<String> = emptyList(),
) {
    val isError: Boolean = errors.isNotEmpty()
    val isEmpty: Boolean = data == null || (data as? List<Any?>)?.isEmpty() == true
}

data class GifScreenState(
    val isFinished: Boolean = false,
    val items: List<GifItem>,
    val query: String = "",
    val page: Int = 0,
)

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
