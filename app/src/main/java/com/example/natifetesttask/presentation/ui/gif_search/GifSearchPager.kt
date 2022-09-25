package com.example.natifetesttask.presentation.ui.gif_search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.natifetesttask.presentation.models.BoundSignal
import com.example.natifetesttask.presentation.models.GifItem
import com.example.natifetesttask.presentation.utils.compose.rememberState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GifSearchPager(
    modifier: Modifier = Modifier,
    items: List<GifItem>,
    imageLoader: ImageLoader,
    onDeleteItem: (String) -> Unit,
    onBoundReached: (BoundSignal) -> Unit,
    currentItemIndex: Int,
    onPageScrolled: (Int) -> Unit,
    onBackPressed: () -> Unit,
) {
    val pagerState = rememberPagerState(currentItemIndex)
    val boundSignal by remember {
        derivedStateOf {
            pagerState.run {
                when {
                    pageCount == 0 -> BoundSignal.NONE
                    pageCount - currentPage <= 10 -> BoundSignal.BOTTOM_REACHED
                    currentPage - 1 <= 10 -> BoundSignal.TOP_REACHED
                    else -> BoundSignal.NONE
                }
            }
        }
    }
    HorizontalPager(
        count = items.size,
        state = pagerState,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Cyan),
    ) { count ->
        GifPagerItem(
            item = items[count],
            imageLoader = imageLoader,
            onDeleteItem = onDeleteItem,
        )
    }
    BackHandler(onBack = onBackPressed)
    LaunchedEffect(pagerState.currentPage) {
        onPageScrolled(pagerState.currentPage)
    }
    LaunchedEffect(boundSignal) {
        if (boundSignal.isBoundReached) onBoundReached(boundSignal)
    }
}

@Composable
private fun GifPagerItem(
    item: GifItem,
    imageLoader: ImageLoader,
    onDeleteItem: (String) -> Unit
) {
    var isLoading by rememberState(false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        AsyncImage(
            model = item.originalUrl,
            imageLoader = imageLoader,
            onLoading = { isLoading = true },
            onError = { isLoading = false },
            onSuccess = { isLoading = false },
            placeholder = rememberAsyncImagePainter(
                model = item.smallUrl,
                imageLoader = imageLoader,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            contentDescription = null
        )
        Text(
            text = item.title,
            style = MaterialTheme.typography.body1,
            color = Color.White,
            modifier = Modifier.padding(top = 30.dp)
        )
        IconButton(
            onClick = { onDeleteItem(item.id) },
            modifier = Modifier
                .padding(top = 30.dp)
                .size(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "delete gif",
                tint = MaterialTheme.colors.onError,
            )
        }
    }
}