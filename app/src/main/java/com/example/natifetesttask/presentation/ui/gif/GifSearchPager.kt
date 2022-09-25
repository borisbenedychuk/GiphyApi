package com.example.natifetesttask.presentation.ui.gif

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.natifetesttask.R
import com.example.natifetesttask.presentation.models.gif.BoundSignal
import com.example.natifetesttask.presentation.models.gif.GifItem
import com.example.natifetesttask.presentation.utils.compose.rememberState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlin.math.abs

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GifSearchPager(
    modifier: Modifier = Modifier,
    items: List<GifItem>,
    imageLoader: ImageLoader,
    onDeleteItem: (String) -> Unit,
    onBoundReached: (BoundSignal) -> Unit,
    initialIndex: Int,
    onPageScrolled: (Int) -> Unit,
    onBackPressed: () -> Unit,
) {
    val pagerState = rememberPagerState(initialIndex)
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
    val currentItem = items[pagerState.currentPage]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        HorizontalPager(
            count = items.size,
            state = pagerState,
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) { count ->
            GifPagerItem(
                item = items[count],
                imageLoader = imageLoader,
            )
        }
        Text(
            text = currentItem.title,
            style = MaterialTheme.typography.body1.copy(fontSize = 20.sp),
            color = Color.White,
            modifier = Modifier
                .padding(top = 30.dp)
                .padding(horizontal = 20.dp)
                .height(LocalDensity.current.run { 20.sp.toDp() * 2.5f })
                .graphicsLayer {
                    alpha = 1f - 2 * abs(pagerState.currentPageOffset)
                },
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        IconButton(
            onClick = { onDeleteItem(currentItem.id) },
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
        BackHandler(onBack = onBackPressed)
        LaunchedEffect(pagerState.currentPage) {
            onPageScrolled(pagerState.currentPage)
        }
        LaunchedEffect(boundSignal) {
            if (boundSignal.isBoundReached) onBoundReached(boundSignal)
        }
    }
}

@Composable
private fun GifPagerItem(
    item: GifItem,
    imageLoader: ImageLoader,
) {
    var isLoading by rememberState(false)
    Box(
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = item.originalUrl,
            imageLoader = imageLoader,
            onLoading = { isLoading = true },
            onError = { isLoading = false },
            onSuccess = { isLoading = false },
            placeholder = rememberAsyncImagePainter(
                model = R.drawable.placeholder,
                imageLoader = imageLoader,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            contentDescription = null,
        )
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.Black,
                modifier = Modifier
                    .fillMaxSize(0.15f)
                    .align(Alignment.TopEnd)
                    .padding(top = 20.dp, end = 20.dp),
                strokeWidth = 5.dp
            )
        }
    }
}