package com.example.gif_apigifs_screen.presentation.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.MapKey
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

@MapKey
annotation class ViewModelKey(val clazz: KClass<out ViewModel>)

class ViewModelFactory @Inject constructor(
    val viewModels: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelProvider = viewModels[modelClass]
            ?: throw Exception("Class $modelClass was not provided to dagger")
        return viewModelProvider.get() as T
    }
}