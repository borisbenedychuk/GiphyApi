package com.example.gif_apigifs_screen.presentation.theme.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalDimensions = staticCompositionLocalOf<Dimensions> { PortraitDimensions() }

object GifApiTheme {

    val dimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current
}

class PortraitDimensions : Dimensions() {
    override val cardImageCornerRadius: Dp = 30.dp
    override val cardImageWidthPercent: Float = 0.75f
    override val cardProgressBarWidthPercent: Float = 0.1f
    override val cardRetryButtonWidthPercent: Float = 0.15f
    override val cardDeleteButtonPadding: Dp = 10.dp
    override val cardDeleteButtonIconPadding: Dp = 12.dp
    override val cardDeleteButtonWidthPercent: Float = 0.15f
    override val footerProgressBarPadding: Dp = 10.dp
    override val footerProgressBarStrokeWidth: Dp = 6.dp
    override val itemsPaddingGifPagerInfo: Dp = 10.dp
}

class LandscapeDimensions : Dimensions() {
    override val cardImageCornerRadius: Dp = 20.dp
    override val cardImageWidthPercent: Float = 0.5f
    override val cardProgressBarWidthPercent: Float = 0.066f
    override val cardRetryButtonWidthPercent: Float = 0.1f
    override val cardDeleteButtonPadding: Dp = 6.dp
    override val cardDeleteButtonIconPadding: Dp = 8.dp
    override val cardDeleteButtonWidthPercent: Float = 0.1f
    override val footerProgressBarPadding: Dp = 6.dp
    override val footerProgressBarStrokeWidth: Dp = 4.dp
    override val itemsPaddingGifPagerInfo: Dp = 30.dp
}

abstract class Dimensions {
    abstract val cardImageCornerRadius: Dp
    abstract val cardImageWidthPercent: Float
    abstract val cardProgressBarWidthPercent: Float
    abstract val cardRetryButtonWidthPercent: Float
    abstract val cardDeleteButtonPadding: Dp
    abstract val cardDeleteButtonIconPadding: Dp
    abstract val cardDeleteButtonWidthPercent: Float
    abstract val footerProgressBarPadding: Dp
    abstract val footerProgressBarStrokeWidth: Dp
    abstract val itemsPaddingGifPagerInfo: Dp
}

