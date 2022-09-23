package com.example.natifetesttask.application

import android.content.Context
import android.os.Build
import androidx.room.Room
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.natifetesttask.data.db.AppDB
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Scope
import kotlin.reflect.KClass

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class Scoped(val clazz: KClass<out Any>)

@Scoped(BasicComponent::class)
@Component(modules = [BasicModule::class])
interface BasicComponent : BasicProvider {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): BasicComponent
    }
}

interface BasicProvider {
    val retrofit: Retrofit
    val appDB: AppDB
    val imageLoader: ImageLoader
}

@Module
class BasicModule {

    @Provides
    @Scoped(BasicComponent::class)
    fun provideDB(context: Context): AppDB =
        Room.databaseBuilder(context, AppDB::class.java, "AppDB")
            .fallbackToDestructiveMigration()
            .build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Scoped(BasicComponent::class)
    fun provideRetrofit(json: Json) =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Scoped(BasicComponent::class)
    fun provideJson() =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }

    @Provides
    @Scoped(BasicComponent::class)
    fun provideImageLoader(context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(1.0)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir)
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

private const val BASE_URL = "https://api.giphy.com/v1/gifs/"