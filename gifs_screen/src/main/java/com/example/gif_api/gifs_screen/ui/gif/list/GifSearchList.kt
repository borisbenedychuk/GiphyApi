package com.example.gif_api.gifs_screen.ui.gif.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.example.gif_api.gifs_screen.models.gif.BoundSignal
import com.example.gif_api.gifs_screen.models.gif.GifItem
import com.example.gif_api.gifs_screen.models.gif.ListPositionInfo
import com.example.gif_api.gifs_screen.ui.gif.LIST_LANDSCAPE_TAG
import com.example.gif_api.gifs_screen.ui.gif.LIST_PORTRAIT_TAG
import com.example.gif_api.gifs_screen.utils.compose.isInLandScape

@Composable
fun GifSearchList(
    items: List<GifItem>,
    showFooter: Boolean,
    errorMsg: String?,
    imageLoader: ImageLoader,
    onDeleteItem: (String) -> Unit,
    onBoundReached: (BoundSignal) -> Unit,
    onRetryClick: () -> Unit,
    onItemClick: (ListPositionInfo) -> Unit,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    initialOffset: Int = 0,
    lazyListState: LazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialPage,
        initialFirstVisibleItemScrollOffset = initialOffset,
    ),
) {
    val boundSignal by remember {
        derivedStateOf {
            lazyListState.layoutInfo.run {
                when {
                    totalItemsCount == 0 -> BoundSignal.NONE
                    totalItemsCount - visibleItemsInfo.last().index + 1 <= 10 -> BoundSignal.BOTTOM_REACHED
                    visibleItemsInfo.first().index - 1 <= 10 -> BoundSignal.TOP_REACHED
                    else -> BoundSignal.NONE
                }
            }
        }
    }
    val lazyListScopeBody: LazyListScope.() -> Unit = {
        gifItems(
            items = items,
            imageLoader = imageLoader,
            onItemClick = { index ->
                onItemClick(
                    ListPositionInfo(
                        itemId = items[index].id,
                        itemOffset = lazyListState
                            .layoutInfo
                            .visibleItemsInfo
                            .find { it.index == index }?.offset ?: 0,
                    )
                )
            },
            onDeleteItem = onDeleteItem,
            showFooter = showFooter,
            errorMsg = errorMsg,
            onRetryClick = onRetryClick,
        )
    }
    if (isInLandScape()) {
        LazyRow(
            state = lazyListState,
            modifier = modifier
                .testTag(LIST_LANDSCAPE_TAG)
                .padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = lazyListScopeBody,
        )
    } else {
        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .testTag(LIST_PORTRAIT_TAG)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 20.dp),
            content = lazyListScopeBody,
        )
    }
    LaunchedEffect(items, boundSignal) {
        if (boundSignal.isBoundReached) onBoundReached(boundSignal)
    }
}
