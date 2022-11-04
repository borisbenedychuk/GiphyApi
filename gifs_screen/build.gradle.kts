plugins {
    id("com.android.library")
    kotlin("plugin.serialization")
    kotlin("android")
    kotlin("kapt")
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
    composeOptions.kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
}

dependencies {
    implementation(projects.domain)
    implementation(projects.coreUi)
    implementation(libs.bundles.compose)
    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
    testImplementation(libs.bundles.unitTesting)
}