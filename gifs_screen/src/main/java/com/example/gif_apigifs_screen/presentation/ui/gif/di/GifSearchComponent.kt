package com.example.gif_apigifs_screen.presentation.ui.gif.di

import coil.ImageLoader
import com.example.gif_apigifs_screen.presentation.utils.ViewModelFactory
import dagger.Component

@Component(
    dependencies = [GifSearchDependencies::class],
    modules = [GifListModule::class],
)
interface GifSearchComponent {
    val viewModelFactory: ViewModelFactory
    val imageLoader: ImageLoader
}





