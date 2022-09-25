package com.example.natifetesttask.app.di

import android.content.Context
import com.example.natifetesttask.app.di.providers.CommonRepositoryDependencies
import com.example.natifetesttask.domain.usecase.gif.DeleteOldDataCacheUseCase
import com.example.natifetesttask.presentation.ui.gif.GifSearchDependencies
import dagger.BindsInstance
import dagger.Component

@Scoped(AppComponent::class)
@Component(
    modules = [
        GifComponentsModule::class,
        CommonModule::class
    ]
)
interface AppComponent : CommonRepositoryDependencies, GifSearchDependencies {

    val deleteOldDataCacheUseCase: DeleteOldDataCacheUseCase

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context,
        ): AppComponent
    }
}







