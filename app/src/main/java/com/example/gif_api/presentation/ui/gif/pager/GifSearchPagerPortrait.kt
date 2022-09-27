package com.example.gif_api.presentation.ui.gif.pager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gif_api.presentation.models.gif.GifItem
import com.example.gif_api.presentation.models.gif.ImageState
import com.example.gif_api.presentation.models.gif.ImageState.*
import com.example.gif_api.presentation.utils.compose.rememberState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GifSearchPagerPortrait(
    items: List<GifItem>,
    imageLoader: ImageLoader,
    onDeleteItem: (String) -> Unit,
    pagerState: PagerState,
    deleteAnimationProgress: Float,
) {
    val currentItem = items[pagerState.currentPage]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        var loadingState by rememberState(LOADING)
        var retryHash by rememberState(0)
        GifPager(
            retryHash = retryHash,
            deleteAnimationProgress = deleteAnimationProgress,
            modifier = Modifier.fillMaxWidth(),
            items = items,
            imageLoader = imageLoader,
            pagerState = pagerState,
            onStateChanged = { loadingState = it }
        )
        GifPagerItemInfo(
            currentItem = currentItem,
            pagerState = pagerState,
            onDeleteItem = onDeleteItem,
            loadingState = loadingState,
            onRetry = { retryHash++ }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GifPager(
    retryHash: Int,
    items: List<GifItem>,
    imageLoader: ImageLoader,
    onStateChanged: (ImageState) -> Unit,
    modifier: Modifier = Modifier,
    deleteAnimationProgress: Float,
    pagerState: PagerState = rememberPagerState(),
) {
    val painter = rememberAsyncImagePainter(
        model = R.drawable.placeholder,
        imageLoader = imageLoader,
    )
    HorizontalPager(
        count = items.size,
        state = pagerState,
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) { count ->
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(items[count].originalUrl)
                .setParameter("retry_hash", retryHash, memoryCacheKey = null)
                .build(),
            imageLoader = imageLoader,
            onLoading = { onStateChanged(LOADING) },
            onError = { onStateChanged(ERROR) },
            onSuccess = { onStateChanged(SUCCESS) },
            placeholder = painter,
            error = painter,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    val alpha = if (currentPage == count) 1f - deleteAnimationProgress else 1f
                    this.alpha = alpha
                }
                .height(400.dp),
            contentDescription = null,
        )
    }
}