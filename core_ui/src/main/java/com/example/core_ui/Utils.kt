package com.example.core_ui

import android.content.Context
import com.example.gif_api.domain.utils.Dependencies
import com.example.gif_api.domain.utils.HasDependencies

inline fun <reified T : Dependencies> Context.getDependencies(): T =
    when (this) {
        is HasDependencies -> dependencies[T::class.java] as T
        else -> (applicationContext as HasDependencies).dependencies[T::class.java] as T
    }