package com.example.gif_api.presentation.ui.gif.di

import coil.ImageLoader
import com.example.gif_api.domain.usecase.gif.AddToBlacklistUseCase
import com.example.gif_api.domain.usecase.gif.GetPagesUseCase

interface GifSearchDependencies {
    val imageLoader: ImageLoader
    val getPagesUseCase: GetPagesUseCase
    val addToBlacklistUseCase: AddToBlacklistUseCase
}