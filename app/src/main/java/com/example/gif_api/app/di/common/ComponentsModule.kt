package com.example.gif_api.app.di.common

import com.example.gif_api.app.di.AppComponent
import com.example.gif_api.data.repositories.gif.di.DaggerGifRepositoryComponent
import com.example.gif_api.data.repositories.gif.di.GifRepositoryComponent
import com.example.gif_api.domain.repository.gif.GifRepository
import dagger.Module
import dagger.Provides

@Module
class ComponentsModule {

    @Provides
    fun provideGifRepository(provider: GifRepositoryComponent): GifRepository =
        provider.gifRepository

    @Provides
    fun provideGifRepositoryComponent(appComponent: AppComponent): GifRepositoryComponent =
        DaggerGifRepositoryComponent.builder().commonRepositoryDependencies(appComponent).build()
}