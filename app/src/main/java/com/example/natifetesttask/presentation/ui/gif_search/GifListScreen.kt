package com.example.natifetesttask.presentation.ui.gif_search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.memory.MemoryCache
import com.example.natifetesttask.application.appComponent
import com.example.natifetesttask.presentation.models.GifItem
import com.example.natifetesttask.presentation.screens.list.DaggerGifListComponent
import com.example.natifetesttask.presentation.ui_utils.compose.rememberState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GifListScreen() {
    val component = getGifListComponent()
    val viewModel = viewModel<GifListViewModel>(factory = component.viewModelFactory)
    GifList(
        gifImageLoader = component.gifImageLoader,
        items = viewModel.gifSearchState.items,
        query = viewModel.gifSearchState.query,
        onNewQuery = viewModel::queryGifs,
        onBoundReached = viewModel::boundReached,
        onDeleteItem = viewModel::deleteItemById,
    )
}

enum class BoundSignal {
    TOP_REACHED,
    BOTTOM_REACHED,
    NONE;

    val isBoundReached: Boolean get() = this == TOP_REACHED || this == BOTTOM_REACHED
}

@Composable
private fun GifList(
    gifImageLoader: ImageLoader,
    items: List<GifItem>,
    query: String,
    onNewQuery: (String) -> Unit,
    onBoundReached: (BoundSignal) -> Unit,
    onDeleteItem: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = query,
            onValueChange = onNewQuery,
        )
        val scope = rememberCoroutineScope()
        var isDetailScreen by rememberState(false)
        var currentItemIndex by rememberState<Int>()
        if (isDetailScreen) {
            GifSearchPager(
                items = items,
                gifLoader = gifImageLoader,
                onDeleteItem = { id ->
                    onDeleteItem(id)
                    val item = items.find { it.id == id } ?: return@GifSearchPager
                    scope.launch { gifImageLoader.deleteGifCoilCache(item) }
                },
                onBoundReached = onBoundReached,
                currentItemIndex = currentItemIndex ?: 0,
                onPageScrolled = { currentItemIndex = it },
                onBackPressed = { isDetailScreen = false }
            )
        } else {
            GifSearchGrid(
                items = items,
                initialPage = currentItemIndex ?: 0,
                gifLoader = gifImageLoader,
                onDeleteItem = { id ->
                    onDeleteItem(id)
                    val item = items.find { it.id == id } ?: return@GifSearchGrid
                    scope.launch { gifImageLoader.deleteGifCoilCache(item) }
                },
                onBoundReached = onBoundReached,
                onItemClick = {
                    currentItemIndex = it
                    isDetailScreen = true
                }
            )
        }
    }
}

@Composable
private fun GifSearchGrid(
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    lazyListState: LazyGridState = rememberLazyGridState(initialPage),
    items: List<GifItem>,
    gifLoader: ImageLoader,
    onDeleteItem: (String) -> Unit,
    onBoundReached: (BoundSignal) -> Unit,
    onItemClick: (Int) -> Unit,
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
    LaunchedEffect(boundSignal) {
        if (boundSignal.isBoundReached) onBoundReached(boundSignal)
    }
    LazyVerticalGrid(
        state = lazyListState,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Cyan),
        columns = GridCells.Adaptive(150.dp)
    ) {
        items(
            items = items,
            contentType = { it.originalUrl },
            key = { it.originalUrl },
        ) { item ->
            var isLoading by rememberState(false)
            Box {
                AsyncImage(
                    model = item.smallUrl,
                    imageLoader = gifLoader,
                    onLoading = { isLoading = true },
                    onError = { isLoading = false },
                    onSuccess = { isLoading = false },
                    placeholder = rememberAsyncImagePainter(
                        model = item.previewUrl,
                        imageLoader = gifLoader,
                    ),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable { onItemClick(items.indexOf(item)) },
                    contentDescription = null,
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .background(Color(0x4D000000), RoundedCornerShape(3.dp))
                            .padding(10.dp)
                            .fillMaxSize(0.1f)
                            .aspectRatio(1f)
                            .align(Alignment.Center),
                        strokeWidth = 3.dp,
                        color = Color.White,
                    )
                } else {
                    IconButton(
                        onClick = { onDeleteItem(item.id) },
                        modifier = Modifier
                            .fillMaxSize(0.2f)
                            .align(Alignment.TopEnd),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "delete gif",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getGifListComponent(): GifListComponent {
    val context = LocalContext.current
    val appComponent = context.appComponent
    return remember {
        DaggerGifListComponent.builder()
            .gifRepositoryProvider(appComponent.provider)
            .basicProvider(appComponent.basicProvider)
            .build()
    }
}

@OptIn(ExperimentalCoilApi::class)
private suspend fun ImageLoader.deleteGifCoilCache(item: GifItem) = withContext(Dispatchers.IO) {
    with(item) {
        listOf(originalUrl, smallUrl, previewUrl).forEach {
            diskCache?.remove(it)
            memoryCache?.remove(MemoryCache.Key(it))
        }
    }
}