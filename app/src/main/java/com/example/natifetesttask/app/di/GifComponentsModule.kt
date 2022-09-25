package com.example.natifetesttask.app.di

import com.example.natifetesttask.data.repositories.gif.di.DaggerGifRepositoryComponent
import com.example.natifetesttask.data.repositories.gif.di.GifRepositoryComponent
import com.example.natifetesttask.domain.repository.gif.GifRepository
import dagger.Module
import dagger.Provides

@Module
class GifComponentsModule {

    @Provides
    fun provideGifRepository(provider: GifRepositoryComponent): GifRepository =
        provider.gifRepository

    @Provides
    fun provideGifRepositoryComponent(appComponent: AppComponent): GifRepositoryComponent =
        DaggerGifRepositoryComponent.builder().commonRepositoryDependencies(appComponent).build()
}