package com.example.gif_apigifs_screen.presentation.models.gif

data class GifSearchState(
    val items: List<GifItem> = emptyList(),
    val errorMsg: String? = null,
    val loading: Boolean = false,
    val query: String = "",
    val showFooter: Boolean = false,
    val isDetailsScreen: Boolean = false,
    val listPositionInfo: ListPositionInfo = ListPositionInfo(),
) {

    val currentIndex: Int =
        items.indexOfFirst { it.id == listPositionInfo.itemId }.coerceAtLeast(0)
}

data class ListPositionInfo(
    val itemId: String = "",
    val itemOffset: Int = 0,
)

