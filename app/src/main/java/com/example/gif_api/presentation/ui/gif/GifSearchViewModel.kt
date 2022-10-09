package com.example.gif_api.presentation.ui.gif

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gif_api.domain.usecase.gif.AddToBlacklistUseCase
import com.example.gif_api.domain.usecase.gif.Pager
import com.example.gif_api.domain.utils.Result
import com.example.gif_api.presentation.models.gif.*
import com.example.gif_api.presentation.models.gif.GifSearchAction.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class GifSearchViewModel @Inject constructor(
    private val pager: Pager,
    private val addToBlacklistUseCase: AddToBlacklistUseCase,
) : ViewModel() {

    var gifSearchState: GifSearchState by mutableStateOf(GifSearchState())
        private set

    private var currentJob: Job? = null
    private var currentPage = 1

    init {
        viewModelScope.launch {
            pager.pagesFlow.collect {
                gifSearchState = gifSearchState.copy(items = it.map { model -> model.asItem() })
            }
        }
    }

    fun handleAction(action: GifSearchAction) {
        when (action) {
            is NewQuery -> queryGifs(action.query)
            is NewCurrentItem -> updateCurrentItemId(action.id)
            is BoundsReached -> boundReached(action.signal)
            is DeleteItem -> deleteItemById(action.id)
            is NavigateToPager -> navigateToDetails(action.info)
            is NavigateToGrid -> navigateToList()
            is RetryLoad -> retryLoad()
        }
    }

    private fun retryLoad() {
        gifSearchState = gifSearchState.copy(errorMsg = null)
        boundReached(BoundSignal.BOTTOM_REACHED)
    }

    private fun updateCurrentItemId(id: String) {
        gifSearchState = gifSearchState.copy(
            listPositionInfo = gifSearchState.listPositionInfo.copy(itemId = id)
        )
    }

    private fun navigateToList() {
        gifSearchState = gifSearchState.copy(isDetailsScreen = false)
    }

    private fun navigateToDetails(info: ListPositionInfo) {
        gifSearchState = gifSearchState.copy(listPositionInfo = info, isDetailsScreen = true)
    }

    private fun queryGifs(query: String) {
        currentJob?.cancel()
        when {
            query.isEmpty() -> {
                gifSearchState = gifSearchState.copy(query = query, loading = false)
            }
            query != gifSearchState.query -> {
                gifSearchState = gifSearchState.copy(query = query, loading = true)
                requestPages(query, 0)
            }
        }
    }

    private fun deleteItemById(id: String) {
        viewModelScope.launch {
            val newItem = gifSearchState.run {
                items.getOrElse(currentIndex - 1) { items.getOrNull(currentIndex + 1) }
            }
            val newId = newItem?.id.orEmpty()
            gifSearchState = gifSearchState.copy(
                listPositionInfo = gifSearchState.listPositionInfo.copy(
                    itemId = newId,
                )
            )
            addToBlacklistUseCase(id)
        }
    }

    private fun boundReached(signal: BoundSignal) {
        val query = gifSearchState.query
        val requestPage = when {
            signal == BoundSignal.BOTTOM_REACHED && gifSearchState.showFooter -> currentPage + 1
            signal == BoundSignal.TOP_REACHED && currentPage > 1 -> currentPage - 1
            else -> return
        }
        requestPages(query, requestPage)
    }

    private fun requestPages(
        query: String,
        requestPage: Int,
    ) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            delay(300)
            when (val result = pager.newPages(query, requestPage)) {
                is Result.Success -> {
                    currentPage = if (requestPage == 0) 1 else requestPage
                    gifSearchState = gifSearchState.copy(showFooter = result.data, loading = false)
                }
                is Result.Error -> {
                    gifSearchState = gifSearchState.copy(
                        errorMsg = result.msg.orEmpty(),
                        loading = false,
                    )
                }
                is Result.Empty -> {
                    gifSearchState = gifSearchState.copy(
                        items = emptyList(),
                        loading = false,
                        showFooter = false,
                    )
                }
            }
        }
    }
}

