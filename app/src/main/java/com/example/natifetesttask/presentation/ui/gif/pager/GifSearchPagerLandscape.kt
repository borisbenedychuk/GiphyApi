package com.example.natifetesttask.presentation.ui.gif.pager

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.example.natifetesttask.presentation.models.gif.BoundSignal
import com.example.natifetesttask.presentation.models.gif.GifItem
import com.example.natifetesttask.presentation.utils.compose.rememberState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GifSearchPagerLandscape(
    pagerState: PagerState,
    items: List<GifItem>,
    imageLoader: ImageLoader,
    onDeleteItem: (String) -> Unit,
) {
    var isLoading by rememberState(false)
    val currentItem = items[pagerState.currentPage]
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
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
                isLoading = isLoading,
            )
        }
        GifPager(
            modifier = Modifier
                .padding(10.dp)
                .weight(1f)
                .fillMaxHeight(),
            items = items,
            imageLoader = imageLoader,
            pagerState = pagerState,
            onLoading = { isLoading = true },
            onFinish = { isLoading = false })
    }
}