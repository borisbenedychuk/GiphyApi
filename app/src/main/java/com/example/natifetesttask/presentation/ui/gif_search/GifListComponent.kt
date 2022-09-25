package com.example.natifetesttask.presentation.ui.gif_search

import androidx.lifecycle.ViewModel
import coil.ImageLoader
import com.example.natifetesttask.app.di.providers.CommonProvider
import com.example.natifetesttask.app.di.providers.GifRepositoryProvider
import com.example.natifetesttask.presentation.utils.ViewModelFactory
import com.example.natifetesttask.presentation.utils.ViewModelKey
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.multibindings.IntoMap

@Component(
    dependencies = [GifRepositoryProvider::class, CommonProvider::class],
    modules = [GifListModule::class],
)
interface GifListComponent {
    val viewModelFactory: ViewModelFactory
    val imageLoader: ImageLoader
}

@Module
abstract class GifListModule {

    @Binds
    @IntoMap
    @ViewModelKey(GifListViewModel::class)
    abstract fun bindViewModel(viewModel: GifListViewModel): ViewModel
}

