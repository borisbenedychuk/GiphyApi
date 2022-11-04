package com.example.gif_apigifs_screen.presentation.ui.gif.di

import coil.ImageLoader
import com.example.gif_api.domain.gif.usecase.AddToBlacklistUseCase
import com.example.gif_api.domain.gif.usecase.Pager
import com.example.gif_api.domain.utils.Dependencies

interface GifSearchDependencies : Dependencies {
    val imageLoader: ImageLoader
    val pagerImpl: Pager
    val addToBlacklistUseCase: AddToBlacklistUseCase
}