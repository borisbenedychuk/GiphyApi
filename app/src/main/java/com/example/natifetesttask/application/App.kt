package com.example.natifetesttask.application

import android.app.Application
import android.content.Context

class App : Application() {
    val component: AppComponent by lazy { DaggerAppComponent.factory().create(this) }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> component
        else -> (applicationContext as App).component
    }