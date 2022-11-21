package com.example.gif_api.gifs_screen.ui.gif.pager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.example.gif_api.gifs_screen.models.gif.GifItem
import com.example.gif_api.gifs_screen.models.gif.ImageState
import com.example.core_ui.PAGER_LANDSCAPE_TAG
import com.example.gif_api.gifs_screen.utils.compose.rememberState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GifSearchPagerLandscape(
    pagerState: PagerState,
    items: List<GifItem>,
    imageLoader: ImageLoader,
    onDeleteItem: (String) -> Unit,
    deleteAnimationProgress: Float,
) {
    var state by rememberState(ImageState.LOADING)
    var retryHash by rememberState(0)
    val currentItem = items[pagerState.currentPage]
    Row(
        modifier = Modifier
            .testTag(PAGER_LANDSCAPE_TAG)
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        horizontalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.2f))
            GifPagerItemInfo(
                currentItem = currentItem,
                pagerState = pagerState,
                onDeleteItem = onDeleteItem,
                loadingState = state,
                onRetry = { retryHash++ }
            )
        }
        GifPager(
            retryHash = retryHash,
            deleteAnimationProgress = deleteAnimationProgress,
            modifier = Modifier
                .padding(10.dp)
                .weight(1f)
                .fillMaxHeight(),
            items = items,
            imageLoader = imageLoader,
            pagerState = pagerState,
            onStateChanged = { state = it },
        )
    }
}