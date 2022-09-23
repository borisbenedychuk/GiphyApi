package com.example.natifetesttask.presentation.screens.list

import androidx.lifecycle.ViewModel
import coil.ImageLoader
import com.example.natifetesttask.application.BasicProvider
import com.example.natifetesttask.application.ViewModelFactory
import dagger.Binds
import dagger.Component
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Component(
    dependencies = [GifRepositoryProvider::class, BasicProvider::class],
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

@MapKey
annotation class ViewModelKey(val clazz: KClass<out ViewModel>)