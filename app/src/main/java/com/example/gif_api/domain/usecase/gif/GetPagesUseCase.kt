package com.example.gif_api.domain.usecase.gif

import com.example.gif_api.domain.model.gif.GifsPagesModel
import com.example.gif_api.domain.repository.gif.GifRepository
import com.example.gif_api.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPagesUseCase @Inject constructor(
    private val repository: GifRepository
) {

    suspend operator fun invoke(query: String, requestedPage: Int): Result<Flow<GifsPagesModel>> {
        val result = when {
            requestedPage != 0 || repository.isGifCacheFresh(query) ->
                repository.loadPageAndCheckIfHasMore(query, requestedPage)
            else -> repository.loadInitialPagesAndCheckIfHasMore(query)
        }
        return getPages(query, requestedPage, result)
    }

    private suspend fun getPages(
        query: String,
        requestedPage: Int,
        loadResult: Result<Boolean>,
    ): Result<Flow<GifsPagesModel>> {
        return when (loadResult) {
            is Result.Error -> loadResult
            is Result.Empty -> loadResult
            is Result.Success -> {
                val hasMorePages = loadResult.data
                val pages = List(3) { i -> requestedPage - 1 + i }.filter { page -> page >= 0 }
                Result.Success(
                    repository.getPages(query, pages).map { models ->
                        GifsPagesModel(
                            isFinished = !hasMorePages,
                            models = models,
                        )
                    }
                )
            }
        }
    }
}