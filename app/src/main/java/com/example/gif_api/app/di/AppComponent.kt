package com.example.gif_api.app.di

import android.content.Context
import com.example.gif_api.app.di.common.CommonModule
import com.example.gif_api.app.di.common.ComponentsModule
import com.example.gif_api.app.di.common.GifUseCaseBindingModule
import com.example.gif_api.data.gif.di.CommonRepositoryDependencies
import com.example.gif_api.domain.gif.usecase.DeleteOldDataCacheUseCase
import com.example.gif_apigifs_screen.presentation.ui.gif.di.GifSearchDependencies
import dagger.BindsInstance
import dagger.Component

@Scoped(AppComponent::class)
@Component(
    modules = [
        ComponentsModule::class,
        CommonModule::class,
        GifUseCaseBindingModule::class,
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







