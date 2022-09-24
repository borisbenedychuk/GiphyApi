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

private const val PAGE = 30

class GifListViewModel @Inject constructor(
    private val repository: GifRepository,
) : ViewModel() {

    var screenState: ScreenState<GifScreenState> by mutableStateOf(ScreenState())
        private set

    private var offset = 0
    private var currentJob: Job? = null

    fun queryGifs(query: String) {
        currentJob?.cancel()
        offset = 0
        screenState = screenState.copy(data = screenState.data?.copy(query = query))
        currentJob = viewModelScope.launch {
            requestGifs(query, 0)
            launch {
                repository.observeGifs(query).collect { models ->
                    val gifState = GifScreenState(
                        query = screenState.data?.query.orEmpty(),
                        items = models.map { it.asItem() },
                    )
                    screenState = ScreenState(data = gifState)
                }
            }
        }
    }

    fun requestMoreGifs() {
        val query = screenState.data?.query ?: return
        requestGifs(query, offset)
    }

    private fun requestGifs(query: String, offset: Int) {
        viewModelScope.launch {
            val result = repository.getGifs(query, PAGE, offset)
            if (result is Result.Error) {
                screenState = screenState.copy(errors = listOf("${result.code} ${result.msg}"))
            } else {
                this@GifListViewModel.offset += PAGE
            }
        }
    }
}

