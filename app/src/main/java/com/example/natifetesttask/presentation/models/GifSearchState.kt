package com.example.natifetesttask.presentation.models

data class GifSearchState(
    val isFinished: Boolean = false,
    val items: List<GifItem> = emptyList(),
    val errors: List<String> = emptyList(),
    val loading: Boolean = false,
    val query: String = "",
    val page: Int = 0,
    val detailsScreen: Boolean = false,
)

data class TransitionInfo(
    val itemOffset: Int = 0,
    val itemIndex: Int = 0,
)

