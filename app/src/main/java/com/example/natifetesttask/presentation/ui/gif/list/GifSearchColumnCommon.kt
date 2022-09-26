package com.example.natifetesttask.presentation.ui.gif.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.natifetesttask.R
import com.example.natifetesttask.presentation.models.gif.GifItem
import com.example.natifetesttask.presentation.models.gif.ImageState
import com.example.natifetesttask.presentation.ui.gif.DeleteIcon
import com.example.natifetesttask.presentation.utils.compose.fillMaxSmallestWidth
import com.example.natifetesttask.presentation.utils.compose.isInLandScape
import com.example.natifetesttask.presentation.utils.compose.rememberInteractionSource
import com.example.natifetesttask.presentation.utils.compose.rememberState

fun LazyListScope.GifListBody(
    items: List<GifItem>,
    imageLoader: ImageLoader,
    onItemClick: (Int) -> Unit,
    onDeleteItem: (String) -> Unit,
    showFooter: Boolean,
) {
    itemsIndexed(
        items = items,
        contentType = { i, item -> item.id },
        key = { i, item -> item.originalUrl },
    ) { i, item ->
        GifSearchColumnItem(
            item = item,
            imageLoader = imageLoader,
            onItemClick = { onItemClick(i) },
            onDeleteItem = onDeleteItem,
        )
    }
    if (showFooter) {
        item {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(vertical = 50.dp)
                    .size(40.dp),
                color = MaterialTheme.colors.primary,
                strokeWidth = 6.dp,
            )
        }
    }
}

@Composable
fun GifSearchColumnItem(
    modifier: Modifier = Modifier,
    item: GifItem,
    imageLoader: ImageLoader,
    onItemClick: () -> Unit,
    onDeleteItem: (String) -> Unit,
) {
    var retryHash by rememberState(0)
    var state by rememberState(ImageState.LOADING)
    Box(
        modifier = Modifier.padding(30.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.smallUrl)
                .setParameter("retry_hash", retryHash, memoryCacheKey = null)
                .build(),
            imageLoader = imageLoader,
            onLoading = { state = ImageState.LOADING },
            onError = { state = ImageState.ERROR },
            onSuccess = { state = ImageState.SUCCESS },
            placeholder = rememberAsyncImagePainter(
                model = R.drawable.placeholder,
                imageLoader = imageLoader,
                filterQuality = FilterQuality.Low,
            ),
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxSmallestWidth(if (isInLandScape()) 0.5f else 0.75f)
                .clip(RoundedCornerShape(if (isInLandScape()) 20.dp else 30.dp))
                .aspectRatio(1f)
                .clickable(
                    enabled = state == ImageState.SUCCESS,
                    onClick = onItemClick,
                    indication = rememberRipple(color = Color.Black),
                    interactionSource = rememberInteractionSource(),
                ),
            contentDescription = null,
        )
        when (state) {
            ImageState.LOADING -> {
                ListLoadingIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSmallestWidth(if (isInLandScape()) 0.066f else 0.1f)
                )
            }
            ImageState.ERROR -> {
                RetryIcon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSmallestWidth(if (isInLandScape()) 0.1f else 0.15f),
                    onRetryClick = { retryHash++ },
                )
            }
            ImageState.SUCCESS -> {
                DeleteIcon(
                    item = item,
                    onDeleteItem = onDeleteItem,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(if (isInLandScape()) 6.dp else 10.dp)
                )
            }
        }
    }
}

@Composable
fun RetryIcon(
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit,
) {
    Icon(
        imageVector = Icons.Default.Refresh,
        contentDescription = "delete gif",
        tint = Color.Black,
        modifier = modifier
            .clip(CircleShape)
            .aspectRatio(1f)
            .shadow(5.dp, CircleShape)
            .clickable(
                enabled = true,
                interactionSource = remember(::MutableInteractionSource),
                indication = rememberRipple(color = Color.Black),
                onClick = onRetryClick,
            )
            .background(color = Color.White, shape = CircleShape)
            .clip(CircleShape)
            .padding(12.dp),
    )
}

@Composable
fun ListLoadingIndicator(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = Modifier
            .background(Color(0x4D000000), RoundedCornerShape(3.dp))
            .padding(if (isInLandScape()) 5.dp else 10.dp)
            .then(modifier)
            .aspectRatio(1f),
        strokeWidth = if (isInLandScape()) 3.dp else 6.dp,
        color = Color.White,
    )
}