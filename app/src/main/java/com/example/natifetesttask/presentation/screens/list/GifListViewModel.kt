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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE = 20

class GifListViewModel @Inject constructor(
    private val repository: GifRepository,
) : ViewModel() {

    var screenState: ScreenState<GifScreenState>? by mutableStateOf(ScreenState(loading = true))
        private set

    private var offset = 0
    private var currentQuery: String = ""
    private var currentJob: Job? = null

    fun queryGifs(query: String) {
        currentJob?.cancel()
        offset = 0
        currentQuery = query
        currentJob = viewModelScope.launch {
            requestGifs(query, 0)
            launch {
                repository.observeGifs(query).collect { models ->
                    val gifState = GifScreenState.fromGifModels(models)
                    screenState = ScreenState(data = gifState)
                }
            }
        }
    }

    fun requestMoreGifs() = requestGifs(currentQuery, offset)

    private fun requestGifs(query: String, offset: Int) {
        viewModelScope.launch {
            val result = repository.getGifs(query = query, PAGE, offset)
            if (result is Result.Error) {
                screenState = screenState?.copy(errors = listOf("${result.code} ${result.msg}"))
            }
        }
    }
}

