package com.example.natifetesttask.application

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.natifetesttask.data.repositories.DaggerGifRepositoryComponent
import com.example.natifetesttask.data.repositories.GifRepositoryComponent
import com.example.natifetesttask.presentation.screens.list.GifRepositoryProvider
import dagger.*
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Qualifier

@Scoped(AppComponent::class)
@Component(modules = [GifComponentsModule::class, BasicComponentsModule::class])
interface AppComponent {
    val provider: GifRepositoryProvider
    val basicProvider: BasicProvider

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            @ApplicationContext
            context: Context,
        ): AppComponent
    }
}

@Qualifier
annotation class ApplicationContext

@Module
class BasicComponentsModule {

    @Provides
    @Scoped(AppComponent::class)
    fun provideBasicComponent(@ApplicationContext context: Context): BasicProvider =
        DaggerBasicComponent.factory().create(context)
}

@Module
abstract class GifComponentsModule {

    @Binds
    abstract fun bindGifRepositoryProvider(provider: GifRepositoryComponent): GifRepositoryProvider

    companion object {
        @Provides
        fun provideGifRepositoryComponent(basicComponent: BasicProvider): GifRepositoryComponent =
            DaggerGifRepositoryComponent.builder().basicProvider(basicComponent).build()
    }
}

class ViewModelFactory @Inject constructor(
    val viewModels: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelProvider =
            viewModels[modelClass] ?: throw Exception("Class not provided to dagger: $modelClass")
        return viewModelProvider.get() as T
    }
}

