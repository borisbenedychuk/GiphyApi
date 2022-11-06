package com.example.gif_api.di

import android.content.Context
import com.example.gif_api.di.common.CommonModule
import com.example.gif_api.di.common.ComponentsModule
import com.example.gif_api.data.gif.di.CommonRepositoryDependencies
import com.example.gif_api.domain.gif.usecase.DeleteOldDataCacheUseCase
import com.example.gif_api.gifs_screen.ui.gif.di.GifSearchDependencies
import dagger.BindsInstance
import dagger.Component

@Scoped(AppComponent::class)
@Component(
    modules = [
        ComponentsModule::class,
        CommonModule::class,
    ]
)
interface AppComponent : CommonRepositoryDependencies, GifSearchDependencies {

    val deleteOldDataCacheUseCase: DeleteOldDataCacheUseCase

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
        ): AppComponent
    }
}







