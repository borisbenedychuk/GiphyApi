package com.example.natifetesttask.app.di

import android.content.Context
import com.example.natifetesttask.app.di.providers.CommonProvider
import com.example.natifetesttask.app.di.providers.GifRepositoryProvider
import dagger.BindsInstance
import dagger.Component

@Scoped(AppComponent::class)
@Component(modules = [GifComponentsModule::class, CommonModule::class])
interface AppComponent : CommonProvider {

    val gifRepository: GifRepositoryProvider

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context,
        ): AppComponent
    }
}







