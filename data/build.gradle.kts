plugins {
    id("com.android.library")
    kotlin("plugin.serialization")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.example.gif_api.data"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 33
        buildConfigField("String", "API_KEY", "\"${property("API_KEY")}\"")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(projects.domain)
    implementation(libs.coroutines)
    implementation(libs.coreKtx)
    implementation(libs.roomKtx)
    implementation(libs.bundles.networking)
    kapt(libs.roomCompiler)
    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
    testImplementation(libs.bundles.unitTesting)
    testImplementation(libs.roboelectric)
    testImplementation(libs.livedata)
    testImplementation(libs.mockWebServer)
}