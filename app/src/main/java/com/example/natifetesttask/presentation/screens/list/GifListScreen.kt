package com.example.natifetesttask.presentation.screens.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.natifetesttask.application.appComponent
import com.example.natifetesttask.presentation.models.GifItem
import com.example.natifetesttask.presentation.ui_utils.compose.rememberState

@Composable
fun GifListScreen() {
    val component = getGifListComponent()
    val viewModel = viewModel<GifListViewModel>(factory = component.viewModelFactory)
    viewModel.screenState.data.let {
        GifList(
            gifImageLoader = component.gifImageLoader,
            list = it?.items.orEmpty(),
            query = it?.query.orEmpty(),
            onNewQuery = viewModel::queryGifs,
            onBoundReached = viewModel::boundChanged,
            onDeleteItem = viewModel::deleteItemById,
        )
    }
}

enum class BoundSignal {
    TOP_REACHED, BOTTOM_REACHED, NONE;

    val isBoundReached: Boolean get() = this == TOP_REACHED || this == BOTTOM_REACHED
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GifList(
    gifImageLoader: ImageLoader,
    list: List<GifItem>,
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
        val lazyListState = rememberLazyGridState()
        val boundSignal by derivedStateOf {
            lazyListState.layoutInfo.run {
                when {
                    totalItemsCount == 0 -> BoundSignal.NONE
                    totalItemsCount - visibleItemsInfo.last().index + 1 <= 10 -> BoundSignal.BOTTOM_REACHED
                    visibleItemsInfo.first().index - 1 <= 10 -> BoundSignal.TOP_REACHED
                    else -> BoundSignal.NONE
                }
            }
        }
        LazyVerticalGrid(
            state = lazyListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Cyan),
            columns = GridCells.Adaptive(150.dp)
        ) {
            items(
                items = list,
                contentType = { it.originalUrl },
                key = { it.originalUrl },
            ) { item ->
                var isLoading by rememberState(false)
                Box {
                    AsyncImage(
                        model = item.smallUrl,
                        imageLoader = gifImageLoader,
                        onLoading = { isLoading = true },
                        onError = { isLoading = false },
                        onSuccess = { isLoading = false },
                        placeholder = rememberAsyncImagePainter(
                            model = item.previewUrl,
                            imageLoader = gifImageLoader,
                        ),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .animateItemPlacement(),
                        contentDescription = null
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
                                .align(Alignment.TopEnd)
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
        LaunchedEffect(boundSignal) {
            if (boundSignal.isBoundReached)
                onBoundReached(boundSignal)
        }
    }
}

@Composable
private fun getGifListComponent(): GifListComponent {
    val context = LocalContext.current
    val appComponent = context.appComponent
    return remember {
        DaggerGifListComponent
            .builder()
            .gifRepositoryProvider(appComponent.provider)
            .basicProvider(appComponent.basicProvider)
            .build()
    }
}