package com.example.gif_api.domain.gif.usecase

import com.example.gif_api.domain.gif.model.GifModel
import com.example.gif_api.domain.gif.model.PagerRequestModel
import com.example.gif_api.domain.gif.repository.GifRepository
import com.example.gif_api.domain.utils.Result
import com.example.gif_api.domain.utils.emptyResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class PagerImpl @Inject constructor(
    private val repository: GifRepository
) : Pager {

    private val _pagesFlow = MutableStateFlow(PagerRequestModel(-1, ""))

    @OptIn(ExperimentalCoroutinesApi::class)
    override val pagesFlow: Flow<List<GifModel>>
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

    override suspend fun newPages(query: String, requestedPage: Int): Result<Boolean> {
        val result = when {
            requestedPage == -1 -> return emptyResult()
            requestedPage != 0 || repository.isGifCacheFresh(query) ->
                repository.loadPageAndCheckIfHasMore(query, requestedPage)
            else -> repository.loadInitialPagesAndCheckIfHasMore(query)
        }
        _pagesFlow.emit(PagerRequestModel(requestedPage, query))
        return result
    }
}