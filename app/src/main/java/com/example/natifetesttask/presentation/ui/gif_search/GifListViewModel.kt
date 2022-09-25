package com.example.natifetesttask.presentation.ui.gif_search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.natifetesttask.domain.repository.GifRepository
import com.example.natifetesttask.domain.utils.Result
import com.example.natifetesttask.presentation.models.GifSearchState
import com.example.natifetesttask.presentation.models.asItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

const val PAGE = 16

class GifListViewModel @Inject constructor(
    private val repository: GifRepository,
) : ViewModel() {

    var gifSearchState: GifSearchState by mutableStateOf(GifSearchState(items = emptyList()))
        private set

    private var offset = 0
    private var currentJob: Job? = null
    private var page = 0

    fun queryGifs(query: String) {
        currentJob?.cancel()
        offset = 0
        page = 0
        gifSearchState = gifSearchState.copy(query = query)
        observePages(query)
    }

    fun deleteItemById(id: String) = viewModelScope.launch { repository.addGifToBlackList(id) }

    fun boundReached(signal: BoundSignal) {
        val query = gifSearchState.query
        if (signal == BoundSignal.BOTTOM_REACHED) {
            page++
        } else if (signal == BoundSignal.TOP_REACHED && page > 2) {
            page--
        }
        observePages(query)
    }

    private fun observePages(query: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            when (val result = repository.getPages(query, page)) {
                is Result.Success -> {
                    result.data?.let { flow ->
                        flow.collect { list ->
                            val newGifSearchState = GifSearchState(
                                query = gifSearchState.query,
                                items = list.map { it.asItem() },
                            )
                            gifSearchState = newGifSearchState
                        }
                    }
                }
                is Result.Error -> {

                }
            }
        }
    }
}

