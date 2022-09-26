package com.example.natifetesttask.presentation.ui.gif.pager

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.natifetesttask.R
import com.example.natifetesttask.presentation.models.gif.BoundSignal
import com.example.natifetesttask.presentation.models.gif.GifItem
import com.example.natifetesttask.presentation.utils.compose.rememberState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.dropWhile

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GifSearchPagerPortrait(
    items: List<GifItem>,
    imageLoader: ImageLoader,
    onDeleteItem: (String) -> Unit,
    pagerState: PagerState,
) {

    val currentItem = items[pagerState.currentPage]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        var isLoading by rememberState(false)
        GifPager(
            modifier = Modifier.fillMaxWidth(),
            items = items,
            imageLoader = imageLoader,
            pagerState = pagerState,
            onLoading = { isLoading = true },
            onFinish = { isLoading = false })
        GifPagerItemInfo(
            currentItem = currentItem,
            pagerState = pagerState,
            onDeleteItem = onDeleteItem,
            isLoading = isLoading,
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GifPager(
    items: List<GifItem>,
    imageLoader: ImageLoader,
    onLoading: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(),
) {
    HorizontalPager(
        count = items.size,
        state = pagerState,
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) { count ->
        AsyncImage(
            model = items[count].originalUrl,
            imageLoader = imageLoader,
            onLoading = { onLoading() },
            onError = { onFinish() },
            onSuccess = { onFinish() },
            placeholder = rememberAsyncImagePainter(
                model = R.drawable.placeholder,
                imageLoader = imageLoader,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            contentDescription = null,
        )
    }
}