package com.example.natifetesttask.presentation.models.gif

data class GifSearchState(
    val items: List<GifItem> = emptyList(),
    val errors: List<String> = emptyList(),
    val loading: Boolean = false,
    val query: String = "",
    val page: Int = 0,
    val showFooter: Boolean = false,
    val isDetailsScreen: Boolean = false,
    val transitionInfo: TransitionInfo = TransitionInfo(),
)

data class TransitionInfo(
    val itemIndex: Int = 0,
    val itemOffset: Int = 0,
)

