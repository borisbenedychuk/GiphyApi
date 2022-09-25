package com.example.natifetesttask.app

import android.app.Application
import android.content.Context
import com.example.natifetesttask.app.di.AppComponent
import com.example.natifetesttask.app.di.DaggerAppComponent

class App : Application() {
    val component: AppComponent by lazy { DaggerAppComponent.factory().create(this) }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> component
        else -> (applicationContext as App).component
    }