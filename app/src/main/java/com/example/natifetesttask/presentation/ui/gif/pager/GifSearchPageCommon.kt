package com.example.natifetesttask.presentation.ui.gif.pager

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.example.natifetesttask.presentation.models.gif.BoundSignal
import com.example.natifetesttask.presentation.models.gif.GifItem
import com.example.natifetesttask.presentation.models.gif.ImageState
import com.example.natifetesttask.presentation.theme.ui.NatifeTheme
import com.example.natifetesttask.presentation.ui.gif.DeleteIcon
import com.example.natifetesttask.presentation.ui.gif.list.RetryIcon
import com.example.natifetesttask.presentation.utils.compose.isInLandScape
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GifSearchPager(
    items: List<GifItem>,
    imageLoader: ImageLoader,
    onDeleteItem: (String) -> Unit,
    onBoundReached: (BoundSignal) -> Unit,
    initialIndex: Int,
    onPageScrolled: (Int) -> Unit,
    onBackPressed: () -> Unit,
) {
    val pagerState = rememberSaveable(items, saver = PagerState.Saver) {
        PagerState(currentPage = initialIndex)
    }
    val boundSignal by remember(pagerState) {
        derivedStateOf {
            pagerState.run {
                when {
                    pageCount == 0 -> BoundSignal.NONE
                    pageCount - currentPage <= 1 -> BoundSignal.BOTTOM_REACHED
                    currentPage - 1 <= 1 -> BoundSignal.TOP_REACHED
                    else -> BoundSignal.NONE
                }
            }
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val deleteAnimation = remember { Animatable(0f) }
    val onDeleteItemImpl: (String) -> Unit = { id ->
        coroutineScope.launch {
            deleteAnimation.animateTo(1f)
            onDeleteItem(id)
            deleteAnimation.animateTo(0f)
        }
    }
    if (isInLandScape()) {
        GifSearchPagerLandscape(
            items = items,
            imageLoader = imageLoader,
            onDeleteItem = onDeleteItemImpl,
            pagerState = pagerState,
            deleteAnimationProgress = deleteAnimation.value,
        )
    } else {
        GifSearchPagerPortrait(
            items = items,
            imageLoader = imageLoader,
            onDeleteItem = onDeleteItemImpl,
            pagerState = pagerState,
            deleteAnimationProgress = deleteAnimation.value,
        )
    }
    BackHandler(onBack = onBackPressed)
    LaunchedEffect(pagerState.currentPage) {
        onPageScrolled(pagerState.currentPage)
    }
    LaunchedEffect(boundSignal) {
        if (boundSignal.isBoundReached) {

            onBoundReached(boundSignal)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GifPagerItemInfo(
    currentItem: GifItem,
    pagerState: PagerState,
    onDeleteItem: (String) -> Unit,
    loadingState: ImageState,
    onRetry: () -> Unit,
) {
    Text(
        text = currentItem.title,
        style = MaterialTheme.typography.body1.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .graphicsLayer {
                alpha = 1f - 2 * abs(pagerState.currentPageOffset)
            },
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    when (loadingState) {
        ImageState.ERROR -> {
            RetryIcon(
                modifier = Modifier
                    .fillMaxWidth(0.15f),
                onRetryClick = onRetry,
            )
        }
        else -> {
            DeleteIcon(
                item = currentItem,
                onDeleteItem = onDeleteItem,
                modifier = Modifier
                    .padding(NatifeTheme.dimensions.itemsPaddingGifPagerInfo)
                    .size(50.dp)
            )
        }
    }
    CircularProgressIndicator(
        modifier = Modifier
            .graphicsLayer {
                alpha = if (loadingState == ImageState.LOADING) 1f else 0f
            }
            .padding(NatifeTheme.dimensions.itemsPaddingGifPagerInfo)
            .size(40.dp),
        strokeWidth = 6.dp,
        color = MaterialTheme.colors.primary,
    )
}