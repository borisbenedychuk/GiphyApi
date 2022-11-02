package com.example.gif_api.domain.utils

interface Dependencies

interface HasDependencies {
    val dependencies: Map<Class<out Dependencies>, Dependencies>
}