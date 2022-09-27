package com.example.gif_api.app.di

import android.content.Context
import com.example.gif_api.app.di.common.CommonModule
import com.example.gif_api.app.di.common.CommonRepositoryDependencies
import com.example.gif_api.app.di.common.ComponentsModule
import com.example.gif_api.domain.usecase.gif.DeleteOldDataCacheUseCase
import com.example.gif_api.presentation.ui.gif.di.GifSearchDependencies
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







