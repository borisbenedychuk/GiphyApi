package com.example.natifetesttask.presentation.ui.gif

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.natifetesttask.domain.usecase.gif.AddToBlacklistUseCase
import com.example.natifetesttask.domain.usecase.gif.GetPagesUseCase
import com.example.natifetesttask.domain.utils.Result
import com.example.natifetesttask.presentation.models.gif.*
import com.example.natifetesttask.presentation.models.gif.GifSearchAction.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

const val PAGE = 25

class GifSearchViewModel @Inject constructor(
    private val getPagesUseCase: GetPagesUseCase,
    private val addToBlacklistUseCase: AddToBlacklistUseCase,
) : ViewModel() {

    var gifSearchState: GifSearchState by mutableStateOf(GifSearchState())
        private set

    private var currentJob: Job? = null
    private var currentPage = 1

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
            transitionInfo = gifSearchState.transitionInfo.copy(itemId = id)
        )
    }

    private fun navigateToList() {
        gifSearchState = gifSearchState.copy(isDetailsScreen = false)
    }

    private fun navigateToDetails(info: TransitionInfo) {
        gifSearchState = gifSearchState.copy(transitionInfo = info, isDetailsScreen = true)
    }

    private fun queryGifs(query: String) {
        currentJob?.cancel()
        when {
            query.isEmpty() -> {
                gifSearchState = gifSearchState.copy(query = query, loading = false)
            }
            query != gifSearchState.query -> {
                gifSearchState = gifSearchState.copy(query = query, loading = true)
                observePages(query, 0)
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
                transitionInfo = gifSearchState.transitionInfo.copy(
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
        observePages(query, requestPage)
    }

    private fun observePages(
        query: String,
        requestPage: Int,
    ) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            delay(300)
            when (val result = getPagesUseCase(query, requestPage)) {
                is Result.Success -> {
                    currentPage = if (requestPage == 0) 1 else requestPage
                    result.data.collect { pagesModel ->
                        gifSearchState = gifSearchState.copy(
                            query = gifSearchState.query,
                            items = pagesModel.models.map { it.asItem() },
                            showFooter = !pagesModel.isFinished,
                            loading = false
                        )
                    }
                }
                is Result.Error -> {
                    gifSearchState = gifSearchState.copy(
                        errorMsg = result.msg.orEmpty()
                    )
                }
            }
        }
    }
}

