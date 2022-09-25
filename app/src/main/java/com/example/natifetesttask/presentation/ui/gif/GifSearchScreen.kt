package com.example.natifetesttask.presentation.ui.gif

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.memory.MemoryCache
import com.example.natifetesttask.app.appComponent
import com.example.natifetesttask.presentation.models.gif.GifItem
import com.example.natifetesttask.presentation.models.gif.GifSearchAction
import com.example.natifetesttask.presentation.models.gif.GifSearchAction.*
import com.example.natifetesttask.presentation.models.gif.GifSearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GifSearchScreen() {
    val component = getGifListComponent()
    val viewModel = viewModel<GifSearchViewModel>(factory = component.viewModelFactory)
    GifSearchUI(
        imageLoader = component.imageLoader,
        state = viewModel.gifSearchState,
        onNewAction = viewModel::handleAction,
    )
}

@Composable
private fun GifSearchUI(
    imageLoader: ImageLoader,
    state: GifSearchState,
    onNewAction: (GifSearchAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val scope = rememberCoroutineScope()
        if (state.isDetailsScreen) {
            GifSearchPager(
                items = state.items,
                imageLoader = imageLoader,
                onDeleteItem = { id ->
                    onNewAction(DeleteItem(id))
                    val item = state.items.find { it.id == id } ?: return@GifSearchPager
                    scope.launch { imageLoader.deleteGifCoilCache(item) }
                },
                onBoundReached = { onNewAction(BoundsReached(it)) },
                initialIndex = state.transitionInfo.itemIndex,
                onPageScrolled = { onNewAction(NewIndex(it)) },
                onBackPressed = { onNewAction(NavigateToGrid) },
            )
        } else {
            TextField(
                value = state.query,
                onValueChange = { onNewAction(NewQuery(it)) },
            )
            GifSearchGrid(
                items = state.items,
                initialPage = state.transitionInfo.itemIndex,
                initialOffsetY = -state.transitionInfo.itemOffset.y,
                imageLoader = imageLoader,
                onDeleteItem = { id ->
                    onNewAction(DeleteItem(id))
                    val item = state.items.find { it.id == id } ?: return@GifSearchGrid
                    scope.launch { imageLoader.deleteGifCoilCache(item) }
                },
                onBoundReached = { onNewAction(BoundsReached(it)) },
                onItemClick = { onNewAction(NavigateToPager(it)) },
            )
        }
    }
}


@Composable
private fun getGifListComponent(): GifSearchComponent {
    val context = LocalContext.current
    val appComponent = context.appComponent
    return remember {
        DaggerGifSearchComponent.builder()
            .gifRepositoryProvider(appComponent.gifRepository)
            .commonProvider(appComponent)
            .build()
    }
}

@OptIn(ExperimentalCoilApi::class)
private suspend fun ImageLoader.deleteGifCoilCache(item: GifItem) = withContext(Dispatchers.IO) {
    with(item) {
        listOf(originalUrl, smallUrl).forEach {
            diskCache?.remove(it)
            memoryCache?.remove(MemoryCache.Key(it))
        }
    }
}