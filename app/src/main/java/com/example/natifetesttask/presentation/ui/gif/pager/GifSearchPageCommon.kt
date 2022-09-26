package com.example.natifetesttask.presentation.ui.gif.pager

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.example.natifetesttask.presentation.models.gif.BoundSignal
import com.example.natifetesttask.presentation.models.gif.GifItem
import com.example.natifetesttask.presentation.ui.gif.DeleteIcon
import com.example.natifetesttask.presentation.utils.compose.isInLandScape
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GifPager(
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
    isLoading: Boolean,
) {
    Text(
        text = currentItem.title,
        style = MaterialTheme.typography.body1.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colors.primary,
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
            color = MaterialTheme.colors.primary,
        )
    }
}