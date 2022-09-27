package com.example.natifetesttask.presentation.ui.gif.di

import coil.ImageLoader
import com.example.natifetesttask.presentation.utils.ViewModelFactory
import dagger.Component

@Component(
    dependencies = [GifSearchDependencies::class],
    modules = [GifListModule::class],
)
interface GifSearchComponent {
    val viewModelFactory: ViewModelFactory
    val imageLoader: ImageLoader
}





