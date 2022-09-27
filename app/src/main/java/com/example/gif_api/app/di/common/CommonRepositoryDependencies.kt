package com.example.gif_api.app.di.common

import com.example.gif_api.data.db.AppDB
import retrofit2.Retrofit

interface CommonRepositoryDependencies {
    val retrofit: Retrofit
    val appDB: AppDB
}