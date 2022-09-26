package com.example.natifetesttask.data.repositories.gif.di

import com.example.natifetesttask.app.di.common.CommonRepositoryDependencies
import com.example.natifetesttask.domain.repository.gif.GifRepository
import dagger.Component

@Component(
    modules = [GifRepositoryModule::class],
    dependencies = [CommonRepositoryDependencies::class],
)
interface GifRepositoryComponent {
    val gifRepository: GifRepository
}

