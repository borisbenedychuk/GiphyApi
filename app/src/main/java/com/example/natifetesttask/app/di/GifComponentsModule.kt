package com.example.natifetesttask.app.di

import com.example.natifetesttask.app.di.providers.GifRepositoryProvider
import com.example.natifetesttask.data.repositories.gif.di.DaggerGifRepositoryComponent
import com.example.natifetesttask.data.repositories.gif.di.GifRepositoryComponent
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class GifComponentsModule {

    @Binds
    abstract fun bindGifRepositoryProvider(provider: GifRepositoryComponent): GifRepositoryProvider

    companion object {
        @Provides
        fun provideGifRepositoryComponent(appComponent: AppComponent): GifRepositoryComponent =
            DaggerGifRepositoryComponent.builder().commonProvider(appComponent).build()
    }
}