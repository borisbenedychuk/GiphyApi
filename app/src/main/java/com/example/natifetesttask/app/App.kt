package com.example.natifetesttask.app

import android.app.Application
import android.content.Context
import com.example.natifetesttask.app.di.AppComponent
import com.example.natifetesttask.app.di.DaggerAppComponent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class App : Application() {

    val component: AppComponent by lazy { DaggerAppComponent.factory().create(this) }

    override fun onCreate() {
        super.onCreate()
        MainScope().launch {
            component.deleteOldDataCacheUseCase()
        }
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> component
        else -> (applicationContext as App).component
    }