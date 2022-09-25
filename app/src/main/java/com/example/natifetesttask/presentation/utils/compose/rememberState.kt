package com.example.natifetesttask.presentation.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <T> rememberState() = remember {
    mutableStateOf<T?>(null)
}

@Composable
fun <T> rememberState(initial: T) = remember {
    mutableStateOf(initial)
}