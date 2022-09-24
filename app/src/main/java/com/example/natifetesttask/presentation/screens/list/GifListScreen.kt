package com.example.natifetesttask.presentation.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
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
            onScrolledToBottom = viewModel::requestMoreGifs,
        )
    }
}

@Composable
private fun GifList(
    gifImageLoader: ImageLoader,
    list: List<GifItem>,
    query: String,
    onNewQuery: (String) -> Unit,
    onScrolledToBottom: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = query,
            onValueChange = onNewQuery,
        )
        val lazyListState = rememberLazyGridState()
        val isScrolledToTheBottom by derivedStateOf {
            lazyListState.layoutInfo.run {
                totalItemsCount > 0 && (totalItemsCount - visibleItemsInfo.last().index) < 15
            }
        }
        LaunchedEffect(isScrolledToTheBottom) {
            if (isScrolledToTheBottom) {
                onScrolledToBottom()
            }
        }
        LazyVerticalGrid(
            state = lazyListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Cyan),
            columns = GridCells.Adaptive(150.dp),
        ) {
            items(
                items = list,
                contentType = { it.gifUrl },
                key = { it.gifUrl },
            ) { address ->
                var isLoading by rememberState(false)
                Box {
                    AsyncImage(
                        model = address.gifUrl,
                        imageLoader = gifImageLoader,
                        onLoading = { isLoading = true },
                        onError = { isLoading = false },
                        onSuccess = { isLoading = false },
                        placeholder = rememberAsyncImagePainter(
                            model = address.previewUrl,
                            imageLoader = gifImageLoader,
                        ),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.aspectRatio(1f),
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
        DaggerGifListComponent
            .builder()
            .gifRepositoryProvider(appComponent.provider)
            .basicProvider(appComponent.basicProvider)
            .build()
    }
}