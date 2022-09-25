package com.example.natifetesttask.app.di.providers

import coil.ImageLoader
import com.example.natifetesttask.data.db.AppDB
import retrofit2.Retrofit

interface CommonRepositoryDependencies {
    val retrofit: Retrofit
    val appDB: AppDB
    val imageLoader: ImageLoader
}