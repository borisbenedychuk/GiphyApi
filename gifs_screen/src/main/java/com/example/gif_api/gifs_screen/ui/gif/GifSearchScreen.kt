package com.example.gif_api.gifs_screen.ui.gif

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.memory.MemoryCache
import com.example.core_ui.GLOBAL_LOADING_TAG
import com.example.core_ui.QUERY_TAG
import com.example.core_ui.getDependencies
import com.example.gif_api.gifs_screen.models.gif.GifItem
import com.example.gif_api.gifs_screen.models.gif.GifSearchAction
import com.example.gif_api.gifs_screen.models.gif.GifSearchAction.*
import com.example.gif_api.gifs_screen.models.gif.GifSearchState
import com.example.gif_api.gifs_screen.ui.gif.di.DaggerGifSearchComponent
import com.example.gif_api.gifs_screen.ui.gif.di.GifSearchComponent
import com.example.gif_api.gifs_screen.ui.gif.list.GifSearchList
import com.example.gif_api.gifs_screen.ui.gif.pager.GifSearchPager
import com.example.gif_api.gifs_screen.utils.compose.fillMaxSmallestWidth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GifSearchScreen(
    component: GifSearchComponent = getGifSearchComponent(),
    viewModel: GifSearchViewModel = viewModel(factory = component.viewModelFactory)
) {
    val state by viewModel.gifSearchState.collectAsState(initial = GifSearchState())
    GifSearchUI(
        imageLoader = component.imageLoader,
        state = state,
        onNewAction = viewModel::handleAction,
    )
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun GifSearchUI(
    imageLoader: ImageLoader,
    state: GifSearchState,
    onNewAction: (GifSearchAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val initialIndex = state.currentIndex
    AnimatedContent(
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        transitionSpec = {
            if (targetState) {
                scaleIn(initialScale = 0.8f) + fadeIn() with slideOutHorizontally { -it }
            } else {
                slideInHorizontally { -it } with scaleOut(targetScale = 0.8f) + fadeOut()
            }
        },
        targetState = state.isDetailsScreen,
    ) { isDetailsScreen ->
        if (isDetailsScreen) {
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
                onBackPressed = { onNewAction(NavigateToList) },
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .testTag(QUERY_TAG)
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
                                    .testTag(GLOBAL_LOADING_TAG)
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
            .gifSearchDependencies(context.getDependencies())
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