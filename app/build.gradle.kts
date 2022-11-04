plugins {
    id("com.android.application")
    kotlin("plugin.serialization")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = 33
    namespace = "com.example.gif_api"
    defaultConfig {
        applicationId = "com.example.gif_api"
        targetSdk = 33
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=enable")
    }
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
}

dependencies {
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.gifsScreen)
    implementation(libs.coreKtx)
    implementation(libs.roomKtx)
    implementation(libs.coilCompose)
    implementation(libs.coilGif)
    implementation(libs.bundles.networking)
    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
}