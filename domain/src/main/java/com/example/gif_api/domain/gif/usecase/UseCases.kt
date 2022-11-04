package com.example.gif_api.domain.gif.usecase

import com.example.gif_api.domain.gif.model.GifModel
import com.example.gif_api.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface AddToBlacklistUseCase {
    suspend operator fun invoke(id: String)
}

interface DeleteOldDataCacheUseCase {
    suspend operator fun invoke()
}

interface Pager {
    val pagesFlow: Flow<List<GifModel>>
    suspend fun newPages(query: String, requestedPage: Int): Result<Boolean>
}

