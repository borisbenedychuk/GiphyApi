package com.example.gif_apigifs_screen.presentation.ui.gif.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.example.gif_apigifs_screen.presentation.models.gif.GifItem
import com.example.gif_apigifs_screen.presentation.models.gif.ListPositionInfo
import com.example.gif_apigifs_screen.presentation.utils.compose.isInLandScape

@Composable
fun GifSearchList(
    items: List<GifItem>,
    showFooter: Boolean,
    errorMsg: String?,
    imageLoader: ImageLoader,
    onDeleteItem: (String) -> Unit,
    onBoundReached: (com.example.gif_apigifs_screen.presentation.models.gif.BoundSignal) -> Unit,
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
                    totalItemsCount == 0 -> com.example.gif_apigifs_screen.presentation.models.gif.BoundSignal.NONE
                    totalItemsCount - visibleItemsInfo.last().index + 1 <= 10 -> com.example.gif_apigifs_screen.presentation.models.gif.BoundSignal.BOTTOM_REACHED
                    visibleItemsInfo.first().index - 1 <= 10 -> com.example.gif_apigifs_screen.presentation.models.gif.BoundSignal.TOP_REACHED
                    else -> com.example.gif_apigifs_screen.presentation.models.gif.BoundSignal.NONE
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
                        itemOffset = lazyListState.layoutInfo.visibleItemsInfo.find { it.index == index }?.offset
                            ?: 0,
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
            modifier = modifier.padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = lazyListScopeBody,
        )
    } else {
        LazyColumn(
            state = lazyListState,
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 20.dp),
            content = lazyListScopeBody,
        )
    }
    LaunchedEffect(items, boundSignal) {
        if (boundSignal.isBoundReached) onBoundReached(boundSignal)
    }
}
