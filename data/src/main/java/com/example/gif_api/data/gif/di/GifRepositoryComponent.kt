package com.example.gif_api.data.gif.di

import com.example.gif_api.domain.gif.repository.GifRepository
import dagger.Component

@Component(
    modules = [GifRepositoryModule::class],
    dependencies = [CommonRepositoryDependencies::class],
)
interface GifRepositoryComponent {
    val gifRepository: GifRepository
}

