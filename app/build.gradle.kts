plugins {
    id("com.android.application")
    id("kotlinx-serialization")
    kotlin("android")
    kotlin("kapt")
}

android {

    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.gif_api"
        minSdk = 21
        targetSdk = 33
        buildConfigField("String", "API_KEY", "\"${property("API_KEY")}\"")
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
    composeOptions.kotlinCompilerExtensionVersion = "1.2.0"
}

dependencies {
    val coreKtx = "1.9.0"
    val compose = "1.2.1"
    val coil = "2.2.1"
    val room = "2.4.3"
    val dagger = "2.44"
    val serialization = "1.4.0"
    val serializationConverter = "0.8.0"
    val navigation = "2.5.2"
    val retrofit = "2.9.0"
    val activityCompose = "1.6.0"
    val lifecycleRuntime = "2.5.1"
    val pagerCompose = "0.26.4-beta"
    implementation("androidx.core:core-ktx:$coreKtx")
    implementation("androidx.compose.ui:ui:$compose")
    implementation("androidx.room:room-ktx:$room")
    implementation("androidx.compose.material:material:$compose")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleRuntime")
    implementation("androidx.activity:activity-compose:$activityCompose")
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.google.accompanist:accompanist-pager:$pagerCompose")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:$serializationConverter")
    implementation("androidx.navigation:navigation-compose:$navigation")
    implementation("io.coil-kt:coil-compose:$coil")
    implementation("io.coil-kt:coil-gif:$coil")
    implementation("com.google.dagger:dagger:$dagger")
    kapt("com.google.dagger:dagger-compiler:$dagger")
    kapt("androidx.room:room-compiler:$room")

}