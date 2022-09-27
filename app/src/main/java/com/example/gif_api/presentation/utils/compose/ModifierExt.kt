package com.example.gif_api.presentation.utils.compose

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

fun Modifier.fillMaxSmallestWidth(fraction: Float = 1.0f) = composed {
    width(LocalConfiguration.current.smallestScreenWidthDp.dp * fraction)
}