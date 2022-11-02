package com.example.gif_api.app.di.common

import com.example.gif_api.app.di.AppComponent
import com.example.gif_api.data.gif.di.DaggerGifRepositoryComponent
import com.example.gif_api.data.gif.di.GifRepositoryComponent
import com.example.gif_api.domain.gif.repository.GifRepository
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