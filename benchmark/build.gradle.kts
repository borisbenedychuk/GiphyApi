@file:Suppress("DSL_SCOPE_VIOLATION")

import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
    with(deps.plugins) {
        alias { androidTest }
        alias { kotlinAndroid }
    }
}

android {
    namespace = "com.example.gif_api.benchmark"
    compileSdk = 33
    defaultConfig {
        minSdk = 23
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        create("benchmark") {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions.jvmTarget = "1.8"
    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
    testOptions.managedDevices.devices.create("pixel6Api31", ManagedVirtualDevice::class) {
        device = "Pixel 6"
        apiLevel = 31
        systemImageSource = "aosp"
    }
}

dependencies {
    implementation(deps.androidXTestExt)
    implementation(deps.uiAutomator)
    implementation(deps.macrobenchmark)
    implementation(deps.baselineProfile)
    implementation(projects.coreUi)
}