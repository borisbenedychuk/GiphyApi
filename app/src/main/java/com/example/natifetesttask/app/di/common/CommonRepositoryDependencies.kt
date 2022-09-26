package com.example.natifetesttask.app.di.common

import com.example.natifetesttask.data.db.AppDB
import retrofit2.Retrofit

interface CommonRepositoryDependencies {
    val retrofit: Retrofit
    val appDB: AppDB
}