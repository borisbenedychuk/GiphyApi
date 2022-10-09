package com.example.gif_api.domain.usecase.gif

import com.example.gif_api.domain.model.gif.GifModel
import com.example.gif_api.domain.model.gif.PagerRequestModel
import com.example.gif_api.domain.repository.gif.GifRepository
import com.example.gif_api.domain.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class Pager @Inject constructor(
    private val repository: GifRepository
) {

    private val _pagesFlow = MutableStateFlow(PagerRequestModel(-1, ""))

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagesFlow: Flow<List<GifModel>>
        get() = _pagesFlow.flatMapLatest { requestModel ->
            repository.getPages(
                query = requestModel.query,
                pages = List(3) { i ->
                    requestModel.requestPage - 1 + i
                }.filter { page ->
                    page >= 0
                }
            )
        }

    suspend fun newPages(query: String, requestedPage: Int): Result<Boolean> {
        val result = when {
            requestedPage != 0 || repository.isGifCacheFresh(query) ->
                repository.loadPageAndCheckIfHasMore(query, requestedPage)
            else -> repository.loadInitialPagesAndCheckIfHasMore(query)
        }
        _pagesFlow.emit(PagerRequestModel(requestedPage, query))
        return result
    }
}