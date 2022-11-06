@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    with(deps.plugins) {
        alias { androidApplication }
        alias { kotlinAndroid }
        alias { kotlinSerialization }
        alias { kotlinKapt }
    }
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
    testOptions.unitTests.isIncludeAndroidResources = true
    composeOptions.kotlinCompilerExtensionVersion = deps.versions.composeCompiler.get()
}

dependencies {
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.gifsScreen)
    implementation(projects.coreUi)
    implementation(deps.coreKtx)
    implementation(deps.roomKtx)
    implementation(deps.coilCompose)
    implementation(deps.coilGif)
    implementation(deps.bundles.networking)
    implementation(deps.dagger)
    kapt(deps.daggerCompiler)
    testImplementation(deps.bundles.unitTesting)
    testImplementation(deps.roboelectric)
    testImplementation(deps.composeTest)
    debugImplementation(deps.composeTestManifest)
}