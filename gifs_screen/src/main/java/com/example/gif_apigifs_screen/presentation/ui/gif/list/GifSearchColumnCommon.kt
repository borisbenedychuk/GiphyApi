package com.example.gif_apigifs_screen.presentation.ui.gif.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gif_api.R.*
import com.example.gif_apigifs_screen.presentation.models.gif.GifItem
import com.example.gif_apigifs_screen.presentation.models.gif.ImageState
import com.example.gif_apigifs_screen.presentation.theme.ui.GifApiTheme
import com.example.gif_apigifs_screen.presentation.ui.gif.DeleteIcon
import com.example.gif_apigifs_screen.presentation.ui.gif.FOOTER_TAG
import com.example.gif_apigifs_screen.presentation.ui.gif.RETRY_TAG
import com.example.gif_apigifs_screen.presentation.utils.compose.fillMaxSmallestWidth
import com.example.gif_apigifs_screen.presentation.utils.compose.rememberInteractionSource
import com.example.gif_apigifs_screen.presentation.utils.compose.rememberState

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.gifItems(
    items: List<GifItem>,
    imageLoader: ImageLoader,
    onItemClick: (Int) -> Unit,
    onDeleteItem: (String) -> Unit,
    onRetryClick: () -> Unit,
    showFooter: Boolean,
    errorMsg: String? = null,
) {
    itemsIndexed(
        items = items,
        contentType = { _, item -> item.id },
        key = { _, item -> item.originalUrl },
    ) { i, item ->
        GifSearchColumnItem(
            modifier = Modifier.animateItemPlacement(),
            item = item,
            imageLoader = imageLoader,
            onItemClick = { onItemClick(i) },
            onDeleteItem = onDeleteItem,
        )
    }
    if (items.isNotEmpty() && showFooter) {
        item {
            if (errorMsg == null) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .testTag(FOOTER_TAG)
                        .padding(vertical = 50.dp)
                        .size(40.dp),
                    color = MaterialTheme.colors.primary,
                    strokeWidth = 6.dp,
                )
            } else {
                RetryIcon(
                    modifier = Modifier
                        .testTag(RETRY_TAG)
                        .padding(vertical = 50.dp)
                        .size(80.dp),
                    onRetryClick = onRetryClick,
                )
            }
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
        modifier = modifier.padding(30.dp)
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
                model = drawable.placeholder,
                imageLoader = imageLoader,
                filterQuality = FilterQuality.Low,
            ),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSmallestWidth(GifApiTheme.dimensions.cardImageWidthPercent)
                .clip(RoundedCornerShape(GifApiTheme.dimensions.cardImageCornerRadius))
                .aspectRatio(1f)
                .clickable(
                    enabled = state == ImageState.SUCCESS,
                    onClick = onItemClick,
                    indication = rememberRipple(color = MaterialTheme.colors.primary),
                    interactionSource = rememberInteractionSource(),
                ),
            contentDescription = null,
        )
        when (state) {
            ImageState.LOADING -> {
                ListLoadingIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSmallestWidth(GifApiTheme.dimensions.cardProgressBarWidthPercent)
                )
            }
            ImageState.ERROR -> {
                RetryIcon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSmallestWidth(GifApiTheme.dimensions.cardRetryButtonWidthPercent),
                    onRetryClick = { retryHash++ },
                )
            }
            ImageState.SUCCESS -> {
                DeleteIcon(
                    item = item,
                    onDeleteItem = onDeleteItem,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(GifApiTheme.dimensions.cardDeleteButtonPadding)
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
        contentDescription = "Delete gif",
        tint = MaterialTheme.colors.primary,
        modifier = modifier
            .clip(CircleShape)
            .aspectRatio(1f)
            .shadow(5.dp, CircleShape)
            .clickable(
                enabled = true,
                interactionSource = remember(::MutableInteractionSource),
                indication = rememberRipple(color = MaterialTheme.colors.primary),
                onClick = onRetryClick,
            )
            .background(color = MaterialTheme.colors.surface, shape = CircleShape)
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
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(3.dp))
            .padding(GifApiTheme.dimensions.footerProgressBarPadding)
            .then(modifier)
            .aspectRatio(1f),
        strokeWidth = GifApiTheme.dimensions.footerProgressBarStrokeWidth,
        color = MaterialTheme.colors.primary,
    )
}