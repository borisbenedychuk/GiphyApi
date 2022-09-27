package com.example.gif_api.app.di.common

import android.content.Context
import android.os.Build
import androidx.room.Room
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.gif_api.app.di.AppComponent
import com.example.gif_api.app.di.Scoped
import com.example.gif_api.data.db.AppDB
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import java.io.File

private const val BASE_URL = "https://api.giphy.com/v1/gifs/"

@Module
class CommonModule {

    @Provides
    @Scoped(AppComponent::class)
    fun provideDB(context: Context): AppDB =
        Room.databaseBuilder(context, AppDB::class.java, "AppDB")
            .fallbackToDestructiveMigration()
            .build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Scoped(AppComponent::class)
    fun provideRetrofit(json: Json) =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Scoped(AppComponent::class)
    fun provideJson() =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    @Provides
    @Scoped(AppComponent::class)
    fun provideGifImageLoader(context: Context): ImageLoader {
        val gifDir = File(context.cacheDir.path, "gifs")
        if (!gifDir.exists()) gifDir.mkdir()
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.5)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(gifDir)
                    .maxSizePercent(0.05)
                    .build()
            }
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
}

