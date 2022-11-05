package com.example.gif_api.gifs_screen.ui.gif.di

import coil.ImageLoader
import com.example.gif_api.gifs_screen.utils.ViewModelFactory
import dagger.Component

@Component(
    dependencies = [GifSearchDependencies::class],
    modules = [GifListModule::class],
)
interface GifSearchComponent {
    val viewModelFactory: ViewModelFactory
    val imageLoader: ImageLoader
}





