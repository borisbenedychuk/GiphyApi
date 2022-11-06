@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    with(deps.plugins) {
        alias { androidLibrary }
        alias { kotlinKapt }
        alias { kotlinAndroid }
        alias { kotlinSerialization }
    }
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
    implementation(deps.coroutines)
    implementation(deps.coreKtx)
    implementation(deps.roomKtx)
    implementation(deps.bundles.networking)
    kapt(deps.roomCompiler)
    implementation(deps.dagger)
    kapt(deps.daggerCompiler)
    testImplementation(deps.bundles.unitTesting)
    testImplementation(deps.roboelectric)
    testImplementation(deps.livedata)
    testImplementation(deps.mockWebServer)
}