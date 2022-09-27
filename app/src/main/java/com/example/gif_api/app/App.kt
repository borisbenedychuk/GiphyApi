package com.example.gif_api.app

import android.app.Application
import android.content.Context
import com.example.gif_api.app.di.AppComponent
import com.example.gif_api.app.di.DaggerAppComponent
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