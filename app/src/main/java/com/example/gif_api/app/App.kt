package com.example.gif_api.app

import android.app.Application
import com.example.gif_api.app.di.AppComponent
import com.example.gif_api.app.di.DaggerAppComponent
import com.example.gif_api.domain.utils.Dependencies
import com.example.gif_api.domain.utils.HasDependencies
import com.example.gif_api.gifs_screen.ui.gif.di.GifSearchDependencies
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class App : Application(), HasDependencies {

    private val component: AppComponent by lazy { DaggerAppComponent.factory().create(this) }

    override val dependencies: Map<Class<out Dependencies>, Dependencies> by lazy {
        mapOf(GifSearchDependencies::class.java to component)
    }

    override fun onCreate() {
        super.onCreate()
        MainScope().launch {
            component.deleteOldDataCacheUseCase()
        }
    }
}