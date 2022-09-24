package com.example.natifetesttask.presentation.screens.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.natifetesttask.domain.repository.GifRepository
import com.example.natifetesttask.domain.utils.Result
import com.example.natifetesttask.presentation.models.GifScreenState
import com.example.natifetesttask.presentation.models.ScreenState
import com.example.natifetesttask.presentation.models.asItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

const val PAGE = 16

class GifListViewModel @Inject constructor(
    private val repository: GifRepository,
) : ViewModel() {

    var screenState: ScreenState<GifScreenState> by mutableStateOf(ScreenState())
        private set

    private var offset = 0
    private var currentJob: Job? = null
    private var page = 0

    fun queryGifs(query: String) {
        currentJob?.cancel()
        offset = 0
        page = 0
        screenState = screenState.copy(data = screenState.data?.copy(query = query))
        observePages(query, page)
    }

    fun deleteItemById(id: String) = viewModelScope.launch { repository.addGifToBlackList(id) }

    fun boundChanged(signal: BoundSignal) {
        val query = screenState.data?.query ?: return
        if (signal == BoundSignal.BOTTOM_REACHED) {
            page++
        } else if (signal == BoundSignal.TOP_REACHED && page > 2) {
            page--
        }
        observePages(query, page)
    }

    private fun observePages(query: String, page: Int) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            when (val result = repository.getPages(query, page)) {
                is Result.Success -> {
                    result.data?.let { flow ->
                        flow.collect { list ->
                            val gifState = GifScreenState(
                                query = screenState.data?.query.orEmpty(),
                                items = list.map { it.asItem() },
                            )
                            screenState = ScreenState(data = gifState)
                        }
                    }
                }
                is Result.Error -> {

                }
            }
        }
    }
}

