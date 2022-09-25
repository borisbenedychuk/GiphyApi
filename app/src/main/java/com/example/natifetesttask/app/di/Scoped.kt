package com.example.natifetesttask.app.di

import javax.inject.Scope
import kotlin.reflect.KClass

@Scope
@Retention(AnnotationRetention.BINARY)
annotation class Scoped(val clazz: KClass<out Any>)