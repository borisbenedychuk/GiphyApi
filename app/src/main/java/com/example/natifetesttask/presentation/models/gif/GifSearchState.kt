package com.example.natifetesttask.presentation.models.gif

data class GifSearchState(
    val items: List<GifItem> = emptyList(),
    val errorMsg: String? = null,
    val loading: Boolean = false,
    val query: String = "",
    val page: Int = 0,
    val showFooter: Boolean = false,
    val isDetailsScreen: Boolean = false,
    val transitionInfo: TransitionInfo = TransitionInfo(),
) {

    val currentIndex: Int =
        items.indexOfFirst { it.id == transitionInfo.itemId }.coerceAtLeast(0)
}

data class TransitionInfo(
    val itemId: String = "",
    val itemOffset: Int = 0,
)

