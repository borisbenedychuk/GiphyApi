package com.example.natifetesttask.presentation.ui.gif

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.natifetesttask.R
import com.example.natifetesttask.presentation.models.gif.BoundSignal
import com.example.natifetesttask.presentation.models.gif.GifItem
import com.example.natifetesttask.presentation.models.gif.TransitionInfo
import com.example.natifetesttask.presentation.utils.compose.rememberState

@Composable
fun GifSearchGrid(
    items: List<GifItem>,
    imageLoader: ImageLoader,
    onDeleteItem: (String) -> Unit,
    onBoundReached: (BoundSignal) -> Unit,
    onItemClick: (TransitionInfo) -> Unit,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    initialOffsetY: Int = 0,
    lazyListState: LazyGridState = rememberLazyGridState(initialPage, initialOffsetY),
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
    LazyVerticalGrid(
        state = lazyListState,
        modifier = modifier.fillMaxWidth(),
        columns = GridCells.Adaptive(150.dp)
    ) {
        itemsIndexed(
            items = items,
            contentType = { i, item -> item.id },
            key = { i, item -> item.originalUrl },
        ) { i, item ->
            GifSearchGridItem(
                item = item,
                imageLoader = imageLoader,
                onItemClick = {
                    onItemClick(
                        TransitionInfo(
                            itemIndex = i,
                            itemOffset = lazyListState.layoutInfo.visibleItemsInfo.find { it.index == i }?.offset
                                ?: IntOffset.Zero,
                        )
                    )
                },
                onDeleteItem = onDeleteItem,
            )
        }
    }
    LaunchedEffect(boundSignal) {
        if (boundSignal.isBoundReached) onBoundReached(boundSignal)
    }
}

@Composable
private fun GifSearchGridItem(
    item: GifItem,
    imageLoader: ImageLoader,
    onItemClick: () -> Unit,
    onDeleteItem: (String) -> Unit,
) {
    var isLoading by rememberState(false)
    Box {
        AsyncImage(
            model = item.smallUrl,
            imageLoader = imageLoader,
            onLoading = { isLoading = true },
            onError = { isLoading = false },
            onSuccess = { isLoading = false },
            placeholder = rememberAsyncImagePainter(
                model = R.drawable.placeholder,
                imageLoader = imageLoader,
                filterQuality = FilterQuality.Low,
            ),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
                .clickable(onClick = onItemClick),
            contentDescription = null,
        )
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .background(Color(0x4D000000), RoundedCornerShape(3.dp))
                    .padding(10.dp)
                    .fillMaxSize(0.1f)
                    .aspectRatio(1f)
                    .align(Alignment.Center),
                strokeWidth = 3.dp,
                color = Color.White,
            )
        } else {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "delete gif",
                tint = Color(0xFF741C1C),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(0.2f)
                    .aspectRatio(1f)
                    .shadow(5.dp, CircleShape)
                    .clickable(
                        enabled = true,
                        interactionSource = remember(::MutableInteractionSource),
                        indication = rememberRipple(color = Color(0xFF993232)),
                        onClick = { onDeleteItem(item.id) },
                    )
                    .background(color = Color.White, shape = CircleShape)
                    .clip(CircleShape)
                    .padding(7.dp)
                    .align(Alignment.TopEnd),
            )
        }
    }
}
