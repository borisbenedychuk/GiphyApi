package com.example.gif_api.gifs_screen.ui.gif.di

import androidx.lifecycle.ViewModel
import com.example.gif_api.gifs_screen.ui.gif.GifSearchViewModel
import com.example.gif_api.gifs_screen.utils.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class GifListModule {

    @Binds
    @IntoMap
    @ViewModelKey(GifSearchViewModel::class)
    abstract fun bindViewModel(viewModel: GifSearchViewModel): ViewModel
}