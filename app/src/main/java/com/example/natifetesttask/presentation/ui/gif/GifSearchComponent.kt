package com.example.natifetesttask.presentation.ui.gif

import androidx.lifecycle.ViewModel
import coil.ImageLoader
import com.example.natifetesttask.domain.usecase.gif.AddToBlacklistUseCase
import com.example.natifetesttask.domain.usecase.gif.GetPagesUseCase
import com.example.natifetesttask.presentation.utils.ViewModelFactory
import com.example.natifetesttask.presentation.utils.ViewModelKey
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.multibindings.IntoMap

@Component(
    dependencies = [GifSearchDependencies::class],
    modules = [GifListModule::class],
)
interface GifSearchComponent {
    val viewModelFactory: ViewModelFactory
    val imageLoader: ImageLoader
}

interface GifSearchDependencies {
    val imageLoader: ImageLoader
    val getPagesUseCase: GetPagesUseCase
    val addToBlacklistUseCase: AddToBlacklistUseCase
}

@Module
abstract class GifListModule {

    @Binds
    @IntoMap
    @ViewModelKey(GifSearchViewModel::class)
    abstract fun bindViewModel(viewModel: GifSearchViewModel): ViewModel
}

