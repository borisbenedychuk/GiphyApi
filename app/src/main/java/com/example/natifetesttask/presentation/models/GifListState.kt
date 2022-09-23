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
) {
    companion object {
        fun fromGifModels(models: List<GifModel>) = GifScreenState(
            items = models.map { GifItem(title = it.title, gifUrl = it.gifUrl) }
        )
    }
}

data class GifItem(
    val title: String,
    val gifUrl: String,
)
