@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    with(deps.plugins) {
        alias { androidLibrary }
        alias { kotlinAndroid }
    }
}

android {
    namespace = "com.example.gif_api"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions.jvmTarget = "1.8"
}

dependencies.implementation(projects.domain)