package com.example.natifetesttask.presentation.models.gif

import androidx.compose.ui.unit.IntOffset

data class GifSearchState(
    val items: List<GifItem> = emptyList(),
    val errors: List<String> = emptyList(),
    val loading: Boolean = false,
    val query: String = "",
    val page: Int = 0,
    val isListFinished: Boolean = false,
    val isDetailsScreen: Boolean = false,
    val transitionInfo: TransitionInfo = TransitionInfo(),
)

data class TransitionInfo(
    val itemIndex: Int = 0,
    val itemOffset: IntOffset = IntOffset.Zero,
)

