@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    with(deps.plugins) {
        alias { androidLibrary }
        alias { kotlinAndroid }
    }
}

android {
    namespace = "com.example.gif_api.ui"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    sourceSets["main"].res.srcDirs("src/main/res", "src/main/res/icon")
    kotlinOptions.jvmTarget = "1.8"
}

dependencies.implementation(projects.domain)