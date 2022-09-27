package com.example.natifetesttask.presentation.ui.gif.di

import coil.ImageLoader
import com.example.natifetesttask.domain.usecase.gif.AddToBlacklistUseCase
import com.example.natifetesttask.domain.usecase.gif.GetPagesUseCase

interface GifSearchDependencies {
    val imageLoader: ImageLoader
    val getPagesUseCase: GetPagesUseCase
    val addToBlacklistUseCase: AddToBlacklistUseCase
}