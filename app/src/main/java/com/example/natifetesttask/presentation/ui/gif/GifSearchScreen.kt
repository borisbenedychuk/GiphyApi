package com.example.natifetesttask.presentation.ui.gif

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val component = getGifSearchComponent()
    val viewModel = viewModel<GifSearchViewModel>(factory = component.viewModelFactory)
    GifSearchUI(
        imageLoader = component.imageLoader,
        state = viewModel.gifSearchState,
        onNewAction = viewModel::handleAction,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun GifSearchUI(
    imageLoader: ImageLoader,
    state: GifSearchState,
    onNewAction: (GifSearchAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    AnimatedContent(
        transitionSpec = {
            if (targetState) {
                scaleIn(initialScale = 0.8f) + fadeIn() with slideOutHorizontally { -it }
            } else {
                slideInHorizontally { -it } with scaleOut(targetScale = 0.8f) + fadeOut()
            }
        },
        targetState = state.isDetailsScreen,
    ) {
        if (it) {
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
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    value = state.query,
                    onValueChange = { onNewAction(NewQuery(it)) },
                )
                if (state.query.isNotBlank()) {
                    GifSearchColumn(
                        items = state.items,
                        initialPage = state.transitionInfo.itemIndex,
                        initialOffset = -state.transitionInfo.itemOffset,
                        imageLoader = imageLoader,
                        onDeleteItem = { id ->
                            onNewAction(DeleteItem(id))
                            val item = state.items.find { it.id == id } ?: return@GifSearchColumn
                            scope.launch { imageLoader.deleteGifCoilCache(item) }
                        },
                        onBoundReached = { onNewAction(BoundsReached(it)) },
                        onItemClick = { onNewAction(NavigateToPager(it)) },
                        showFooter = state.showFooter,
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Start typing",
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}


@Composable
private fun getGifSearchComponent(): GifSearchComponent {
    val context = LocalContext.current
    return remember {
        DaggerGifSearchComponent.builder()
            .gifSearchDependencies(context.appComponent)
            .build()
    }
}

@OptIn(ExperimentalCoilApi::class)
private suspend fun ImageLoader.deleteGifCoilCache(item: GifItem) =
    withContext(Dispatchers.IO) {
        with(item) {
            listOf(originalUrl, smallUrl).forEach {
                diskCache?.remove(it)
                memoryCache?.remove(MemoryCache.Key(it))
            }
        }
    }