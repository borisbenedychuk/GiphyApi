package com.example.natifetesttask.data.repositories.gif.di

import com.example.natifetesttask.data.datasources.gif.CacheGifDatasource
import com.example.natifetesttask.data.datasources.gif.CacheGifDatasourceImpl
import com.example.natifetesttask.data.datasources.gif.RemoteGifDatasource
import com.example.natifetesttask.data.datasources.gif.RemoteGifDatasourceImpl
import com.example.natifetesttask.data.datasources.gif_info.CacheGifInfoDatasource
import com.example.natifetesttask.data.datasources.gif_info.CacheGifInfoDatasourceImpl
import com.example.natifetesttask.data.db.AppDB
import com.example.natifetesttask.data.db.dao.GifDao
import com.example.natifetesttask.data.db.dao.GifInfoDao
import com.example.natifetesttask.data.remote.api.GifApi
import com.example.natifetesttask.data.repositories.gif.GifRepositoryImpl
import com.example.natifetesttask.domain.repository.gif.GifRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
abstract class GifRepositoryModule {

    @Binds
    abstract fun bindRepository(repositoryImpl: GifRepositoryImpl): GifRepository

    @Binds
    abstract fun bindCacheDataSource(datasource: CacheGifDatasourceImpl): CacheGifDatasource

    @Binds
    abstract fun bindCacheInfoDataSource(datasource: CacheGifInfoDatasourceImpl): CacheGifInfoDatasource

    @Binds
    abstract fun bindRemoteDatasource(datasource: RemoteGifDatasourceImpl): RemoteGifDatasource

    companion object {

        @Provides
        fun provideGifApi(retrofit: Retrofit): GifApi = retrofit.create(GifApi::class.java)

        @Provides
        fun provideGifDao(database: AppDB): GifDao = database.gifDao

        @Provides
        fun provideGifInfoDao(database: AppDB): GifInfoDao = database.gifInfoDao
    }
}