package com.example.gif_apigifs_screen.presentation.ui.gif

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gif_api.domain.gif.usecase.AddToBlacklistUseCase
import com.example.gif_api.domain.gif.usecase.Pager
import com.example.gif_api.domain.utils.Result
import com.example.gif_apigifs_screen.presentation.models.gif.*
import com.example.gif_apigifs_screen.presentation.models.gif.GifSearchAction.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GifSearchViewModel @Inject constructor(
    private val pager: Pager,
    private val addToBlacklistUseCase: AddToBlacklistUseCase,
) : ViewModel() {

    private val _gifSearchState = MutableStateFlow(GifSearchState())
    val gifSearchState: Flow<GifSearchState> get() = _gifSearchState

    private var currentJob: Job? = null
    private var currentPage = 0

    init {
        viewModelScope.launch {
            pager.pagesFlow.collect {
                _gifSearchState.update { state ->
                    state.copy(items = it.map { model -> model.asItem() })
                }
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
            is NavigateToList -> navigateToList()
            is RetryLoad -> retryLoad()
        }
    }

    private fun retryLoad() {
        _gifSearchState.update { it.copy(errorMsg = null) }
        boundReached(BoundSignal.BOTTOM_REACHED)
    }

    private fun updateCurrentItemId(id: String) {
        _gifSearchState.update {
            it.copy(listPositionInfo = it.listPositionInfo.copy(itemId = id))
        }
    }

    private fun navigateToList() {
        _gifSearchState.update { it.copy(isDetailsScreen = false) }
    }

    private fun navigateToDetails(info: ListPositionInfo) {
        _gifSearchState.update { it.copy(listPositionInfo = info, isDetailsScreen = true) }
    }

    private fun queryGifs(query: String) {
        currentJob?.cancel()
        when {
            query.isEmpty() -> {
                _gifSearchState.update { it.copy(query = query, loading = false) }
            }
            query != _gifSearchState.value.query -> {
                _gifSearchState.update { it.copy(query = query, loading = true) }
                requestPages(query, 0)
            }
        }
    }

    private fun deleteItemById(id: String) {
        viewModelScope.launch {
            val newItem = _gifSearchState.value.run {
                items.getOrElse(currentIndex - 1) { items.getOrNull(currentIndex + 1) }
            }
            val newId = newItem?.id.orEmpty()
            _gifSearchState.update {
                it.copy(listPositionInfo = it.listPositionInfo.copy(itemId = newId))
            }
            addToBlacklistUseCase(id)
        }
    }

    private fun boundReached(signal: BoundSignal) {
        val query = _gifSearchState.value.query
        val requestPage = when {
            signal == BoundSignal.BOTTOM_REACHED && _gifSearchState.value.showFooter -> currentPage + 1
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
                    _gifSearchState.update { it.copy(showFooter = result.data, loading = false) }
                }
                is Result.Error -> {
                    _gifSearchState.update {
                        it.copy(
                            errorMsg = result.msg.orEmpty(),
                            loading = false,
                        )
                    }
                }
                is Result.Empty -> {
                    _gifSearchState.update {
                        it.copy(
                            items = emptyList(),
                            loading = false,
                            showFooter = false,
                        )
                    }
                }
            }
        }
    }
}

