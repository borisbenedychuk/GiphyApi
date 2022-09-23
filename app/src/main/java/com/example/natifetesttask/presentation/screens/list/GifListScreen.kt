package com.example.natifetesttask.presentation.screens.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.natifetesttask.application.appComponent
import com.example.natifetesttask.presentation.models.GifItem
import com.example.natifetesttask.presentation.ui_utils.compose.rememberState

@Composable
fun GifListScreen() {
    val component = getGifListComponent()
    val viewModel = viewModel<GifListViewModel>(factory = component.viewModelFactory)
    viewModel.screenState?.let {
        GifList(
            imageLoader = component.imageLoader,
            list = it.data?.items ?: emptyList(),
            onNewQuery = viewModel::queryGifs
        )
    }
}

@Composable
fun GifList(imageLoader: ImageLoader, list: List<GifItem>, onNewQuery: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        var text by rememberState("")
        TextField(
            value = text,
            onValueChange = {
                onNewQuery(it)
                text = it
            },
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Cyan)
        ) {
            items(list) { address ->
                Image(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(200.dp),
                    painter = rememberAsyncImagePainter(
                        model = address.gifUrl,
                        imageLoader = imageLoader
                    ),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun getGifListComponent(): GifListComponent {
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