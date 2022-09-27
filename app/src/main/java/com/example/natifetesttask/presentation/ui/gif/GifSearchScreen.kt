package com.example.natifetesttask.presentation.ui.gif

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.natifetesttask.presentation.ui.gif.di.DaggerGifSearchComponent
import com.example.natifetesttask.presentation.ui.gif.di.GifSearchComponent
import com.example.natifetesttask.presentation.ui.gif.list.GifSearchList
import com.example.natifetesttask.presentation.ui.gif.pager.GifSearchPager
import com.example.natifetesttask.presentation.utils.compose.fillMaxSmallestWidth
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
    val initialIndex = state.currentIndex
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
                initialIndex = initialIndex,
                onPageScrolled = { onNewAction(NewCurrentItem(state.items[it].id)) },
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
                        .fillMaxSmallestWidth(0.75f),
                    value = state.query,
                    onValueChange = { text ->
                        onNewAction(NewQuery(text))
                    },
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (state.query.isNotBlank()) {
                        GifSearchList(
                            items = state.items,
                            initialPage = initialIndex,
                            initialOffset = -state.listPositionInfo.itemOffset,
                            imageLoader = imageLoader,
                            onDeleteItem = { id ->
                                onNewAction(DeleteItem(id))
                                val item = state.items.find { it.id == id } ?: return@GifSearchList
                                scope.launch { imageLoader.deleteGifCoilCache(item) }
                            },
                            onBoundReached = { signal -> onNewAction(BoundsReached(signal)) },
                            onItemClick = { info -> onNewAction(NavigateToPager(info)) },
                            showFooter = state.showFooter,
                            errorMsg = state.errorMsg,
                            onRetryClick = { onNewAction(RetryLoad) }
                        )
                    } else {
                        Text(
                            text = "Start typing",
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        state.loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colors.surface.copy(alpha = 0.8f))
                                .pointerInput(Unit) { },
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(50.dp)
                                    .align(Alignment.Center),
                                color = MaterialTheme.colors.primary,
                                strokeWidth = 8.dp
                            )
                        }
                    }
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