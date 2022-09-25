package com.example.natifetesttask.data.repositories.gif.di

import com.example.natifetesttask.app.di.providers.CommonProvider
import com.example.natifetesttask.app.di.providers.GifRepositoryProvider
import dagger.Component

@Component(modules = [GifRepositoryModule::class], dependencies = [CommonProvider::class])
interface GifRepositoryComponent : GifRepositoryProvider

