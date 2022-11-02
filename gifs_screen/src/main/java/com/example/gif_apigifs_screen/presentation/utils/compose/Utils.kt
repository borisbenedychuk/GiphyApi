package com.example.gif_apigifs_screen.presentation.utils.compose

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun <T> rememberState() = remember {
    mutableStateOf<T?>(null)
}

@Composable
fun <T> rememberState(initial: T) = remember {
    mutableStateOf(initial)
}

@Composable
fun rememberInteractionSource() = remember {
    MutableInteractionSource()
}

@Composable
fun isInLandScape() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE