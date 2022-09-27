package com.example.gif_api.presentation.ui.gif.di

import androidx.lifecycle.ViewModel
import com.example.gif_api.presentation.ui.gif.GifSearchViewModel
import com.example.gif_api.presentation.utils.ViewModelKey
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