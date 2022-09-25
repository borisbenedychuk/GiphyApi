package com.example.natifetesttask.application

import coil.ImageLoader
import com.example.natifetesttask.data.db.AppDB
import retrofit2.Retrofit

interface BasicProvider {
    val retrofit: Retrofit
    val appDB: AppDB
    val imageLoader: ImageLoader
}