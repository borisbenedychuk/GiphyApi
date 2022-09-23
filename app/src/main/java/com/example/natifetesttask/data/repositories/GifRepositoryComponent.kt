package com.example.natifetesttask.data.repositories

import com.example.natifetesttask.application.BasicProvider
import com.example.natifetesttask.data.datasources.gif.CacheGifDatasource
import com.example.natifetesttask.data.datasources.gif.CacheGifDatasourceImpl
import com.example.natifetesttask.data.datasources.gif.RemoteGifDatasource
import com.example.natifetesttask.data.datasources.gif.RemoteGifDatasourceImpl
import com.example.natifetesttask.data.db.AppDB
import com.example.natifetesttask.data.db.dao.GifDao
import com.example.natifetesttask.data.remote.api.GifApi
import com.example.natifetesttask.domain.repository.GifRepository
import com.example.natifetesttask.presentation.screens.list.GifRepositoryProvider
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Component(modules = [GifRepositoryModule::class], dependencies = [BasicProvider::class])
interface GifRepositoryComponent : GifRepositoryProvider

@Module
abstract class GifRepositoryModule {

    @Binds
    abstract fun bindRepository(repositoryImpl: GifRepositoryImpl): GifRepository

    @Binds
    abstract fun bindCacheDataSource(datasource: CacheGifDatasourceImpl): CacheGifDatasource

    @Binds
    abstract fun bindRemoteDatasource(datasource: RemoteGifDatasourceImpl): RemoteGifDatasource

    companion object {
        @Provides
        fun provideGifApi(retrofit: Retrofit): GifApi = retrofit.create(GifApi::class.java)

        @Provides
        fun provideGifDao(database: AppDB): GifDao = database.gifDao
    }
}