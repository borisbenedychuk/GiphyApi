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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes.create("benchmark") {
        signingConfig = signingConfigs.getByName("debug")
        matchingFallbacks += listOf("release")
        isDebuggable = false
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
    composeOptions.kotlinCompilerExtensionVersion = deps.versions.composeCompiler.get()
}

dependencies {
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.gifsScreen)
    implementation(projects.coreUi)
    implementation(deps.coreKtx)
    implementation(deps.roomKtx)
    implementation(deps.composeActivity)
    implementation(deps.coilGif)
    implementation(deps.bundles.networking)
    implementation(deps.dagger)
    androidTestImplementation(deps.bundles.androidXTest)
    androidTestImplementation(deps.truth)
    androidTestImplementation(deps.composeTest)
    kapt(deps.daggerCompiler)
}