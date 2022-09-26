package com.example.natifetesttask.presentation.ui.gif

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.natifetesttask.R
import com.example.natifetesttask.presentation.models.gif.BoundSignal
import com.example.natifetesttask.presentation.models.gif.GifItem
import com.example.natifetesttask.presentation.models.gif.TransitionInfo
import com.example.natifetesttask.presentation.utils.compose.rememberInteractionSource
import com.example.natifetesttask.presentation.utils.compose.rememberState

@Composable
fun GifSearchColumn(
    items: List<GifItem>,
    showFooter: Boolean,
    imageLoader: ImageLoader,
    onDeleteItem: (String) -> Unit,
    onBoundReached: (BoundSignal) -> Unit,
    onItemClick: (TransitionInfo) -> Unit,
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
    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 20.dp)
    ) {
        itemsIndexed(
            items = items,
            contentType = { i, item -> item.id },
            key = { i, item -> item.originalUrl },
        ) { i, item ->
            GifSearchColumnItem(
                item = item,
                imageLoader = imageLoader,
                onItemClick = {
                    onItemClick(
                        TransitionInfo(
                            itemIndex = i,
                            itemOffset = lazyListState.layoutInfo.visibleItemsInfo.find { it.index == i }?.offset
                                ?: 0,
                        )
                    )
                },
                onDeleteItem = onDeleteItem,
            )
        }
        if (showFooter) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(vertical = 50.dp)
                        .size(40.dp),
                    color = MaterialTheme.colors.primary,
                    strokeWidth = 6.dp,
                )
            }
        }
    }
    LaunchedEffect(items, boundSignal) {
        if (boundSignal.isBoundReached)
            onBoundReached(boundSignal)
    }
}

enum class ImageState {
    ERROR, SUCCESS, LOADING
}

@Composable
private fun GifSearchColumnItem(
    item: GifItem,
    imageLoader: ImageLoader,
    onItemClick: () -> Unit,
    onDeleteItem: (String) -> Unit,
) {
    var retryHash by rememberState(0)
    var state by rememberState(ImageState.LOADING)
    Box(modifier = Modifier.padding(30.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.smallUrl)
                .setParameter("retry_hash", retryHash, memoryCacheKey = null)
                .build(),
            imageLoader = imageLoader,
            onLoading = { state = ImageState.LOADING },
            onError = { state = ImageState.ERROR },
            onSuccess = { state = ImageState.SUCCESS },
            placeholder = rememberAsyncImagePainter(
                model = R.drawable.placeholder,
                imageLoader = imageLoader,
                filterQuality = FilterQuality.Low,
            ),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .fillMaxWidth(0.7f)
                .aspectRatio(1f)
                .clickable(
                    enabled = state == ImageState.SUCCESS,
                    onClick = onItemClick,
                    indication = rememberRipple(color = Color.Black),
                    interactionSource = rememberInteractionSource(),
                ),
            contentDescription = null,
        )
        when (state) {
            ImageState.LOADING -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(Color(0x4D000000), RoundedCornerShape(3.dp))
                        .padding(10.dp)
                        .fillMaxSize(0.1f)
                        .aspectRatio(1f)
                        .align(Alignment.Center),
                    strokeWidth = 6.dp,
                    color = Color.White,
                )
            }
            ImageState.ERROR -> {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "delete gif",
                    tint = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth(0.15f)
                        .clip(CircleShape)
                        .align(Alignment.Center)
                        .aspectRatio(1f)
                        .shadow(5.dp, CircleShape)
                        .clickable(
                            enabled = true,
                            interactionSource = remember(::MutableInteractionSource),
                            indication = rememberRipple(color = Color.Black),
                            onClick = {
                                retryHash++
                            },
                        )
                        .background(color = Color.White, shape = CircleShape)
                        .clip(CircleShape)
                        .padding(12.dp),
                )
            }
            ImageState.SUCCESS -> {
                DeleteIcon(
                    item = item,
                    onDeleteItem = onDeleteItem,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .fillMaxWidth(0.15f)
                )
            }
        }
    }
}

@Composable
fun DeleteIcon(
    item: GifItem,
    onDeleteItem: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = "delete gif",
        tint = Color(0xFFF80000),
        modifier = modifier
            .aspectRatio(1f)
            .shadow(5.dp, CircleShape)
            .clickable(
                enabled = true,
                interactionSource = remember(::MutableInteractionSource),
                indication = rememberRipple(color = Color(0xFFFF0303)),
                onClick = { onDeleteItem(item.id) },
            )
            .background(color = Color.White, shape = CircleShape)
            .clip(CircleShape)
            .padding(12.dp),
    )
}
