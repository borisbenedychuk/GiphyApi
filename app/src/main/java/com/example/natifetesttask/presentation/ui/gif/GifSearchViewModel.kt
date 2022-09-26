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
import kotlinx.coroutines.launch
import javax.inject.Inject

const val PAGE = 20

class GifSearchViewModel @Inject constructor(
    private val getPagesUseCase: GetPagesUseCase,
    private val addToBlacklistUseCase: AddToBlacklistUseCase,
) : ViewModel() {

    var gifSearchState: GifSearchState by mutableStateOf(GifSearchState())
        private set

    private var currentJob: Job? = null
    private var currentPage = 0

    fun handleAction(action: GifSearchAction) {
        when (action) {
            is NewQuery -> queryGifs(action.query)
            is NewCurrentItem -> updateCurrentItemId(action.id)
            is BoundsReached -> boundReached(action.signal)
            is ChangeFocus -> updateIsTyping(action.isTyping)
            is DeleteItem -> deleteItemById(action.id)
            is NavigateToPager -> navigateToDetails(action.info)
            is NavigateToGrid -> navigateToList()
        }
    }

    private fun updateIsTyping(isTyping: Boolean) {
        gifSearchState = gifSearchState.copy(isTyping = isTyping)
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
        if (query != gifSearchState.query) {
            currentJob?.cancel()
            currentPage = 0
            gifSearchState = gifSearchState.copy(query = query)
            observePages(query)
        }
    }

    private fun deleteItemById(id: String) {
        viewModelScope.launch { addToBlacklistUseCase(id) }
    }

    private fun boundReached(signal: BoundSignal) {
        val query = gifSearchState.query
        when {
            signal == BoundSignal.BOTTOM_REACHED -> currentPage++
            signal == BoundSignal.TOP_REACHED && currentPage > 1 -> currentPage--
            else -> return
        }
        observePages(query)
    }

    private fun observePages(query: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            when (val result = getPagesUseCase(query, currentPage)) {
                is Result.Success -> {
                    result.data.collect { pagesModel ->
                        gifSearchState = gifSearchState.copy(
                            query = gifSearchState.query,
                            items = pagesModel.models.map { it.asItem() },
                            showFooter = !pagesModel.isFinished,
                        )
                    }
                }
                is Result.Error -> {

                }
            }
        }
    }
}

