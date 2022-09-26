package com.example.natifetesttask.presentation.ui.gif

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
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
import com.google.accompanist.pager.PagerState
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
        var isLoading by rememberState(false)
        GifPager(
            items = items,
            imageLoader = imageLoader,
            pagerState = pagerState,
            onLoading = { isLoading = true },
            onFinish = { isLoading = false })
        Text(
            text = currentItem.title,
            style = MaterialTheme.typography.body1.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
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
        DeleteIcon(
            item = currentItem,
            onDeleteItem = onDeleteItem,
            modifier = Modifier
                .fillMaxWidth(0.15f)
                .padding(top = 30.dp)
        )
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .size(40.dp),
                strokeWidth = 6.dp,
                color = Color.White,
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

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun GifPager(
    items: List<GifItem>,
    pagerState: PagerState = rememberPagerState(),
    imageLoader: ImageLoader,
    onLoading: () -> Unit,
    onFinish: () -> Unit,
) {
    HorizontalPager(
        count = items.size,
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
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