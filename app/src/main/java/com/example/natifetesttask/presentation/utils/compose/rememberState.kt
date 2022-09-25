package com.example.natifetesttask.presentation.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <T : Any> rememberState() = remember {
    mutableStateOf<T?>(null)
}

@Composable
fun <T : Any> rememberState(initial: T) = remember {
    mutableStateOf(initial)
}