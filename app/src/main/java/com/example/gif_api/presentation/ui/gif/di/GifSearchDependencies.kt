package com.example.gif_api.presentation.ui.gif.di

import coil.ImageLoader
import com.example.gif_api.domain.usecase.gif.AddToBlacklistUseCase
import com.example.gif_api.domain.usecase.gif.Pager

interface GifSearchDependencies {
    val imageLoader: ImageLoader
    val pager: Pager
    val addToBlacklistUseCase: AddToBlacklistUseCase
}