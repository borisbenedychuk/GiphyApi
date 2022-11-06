@file:Suppress("DSL_SCOPE_VIOLATION")
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    with(deps.plugins) {
        alias { androidApplication } apply false
        alias { androidLibrary } apply false
        alias { kotlinJvm } apply false
        alias { kotlinAndroid } apply false
        alias { kotlinSerialization } apply false
        alias { kotlinKapt } apply false
    }
}