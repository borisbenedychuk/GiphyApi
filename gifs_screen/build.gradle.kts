@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    with(deps.plugins) {
        alias { androidLibrary }
        alias { kotlinAndroid }
        alias { kotlinSerialization }
        alias { kotlinKapt }
    }
}

android {
    namespace = "com.example.gif_api.gif_screen"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 33
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
    implementation(projects.domain)
    implementation(projects.coreUi)
    implementation(deps.bundles.compose)
    implementation(deps.dagger)
    kapt(deps.daggerCompiler)
    testImplementation(deps.bundles.unitTesting)
    testImplementation(deps.roboelectric)
    testImplementation(deps.composeTest)
    debugImplementation(deps.composeTestManifest)
}