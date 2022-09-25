package com.example.natifetesttask.presentation.ui_utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class ViewModelFactory @Inject constructor(
    val viewModels: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelProvider =
            viewModels[modelClass] ?: throw Exception("Class not provided to dagger: $modelClass")
        return viewModelProvider.get() as T
    }
}