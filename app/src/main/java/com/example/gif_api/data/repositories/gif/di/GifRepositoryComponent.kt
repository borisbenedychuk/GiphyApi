package com.example.gif_api.data.repositories.gif.di

import com.example.gif_api.app.di.common.CommonRepositoryDependencies
import com.example.gif_api.domain.repository.gif.GifRepository
import dagger.Component

@Component(
    modules = [GifRepositoryModule::class],
    dependencies = [CommonRepositoryDependencies::class],
)
interface GifRepositoryComponent {
    val gifRepository: GifRepository
}

