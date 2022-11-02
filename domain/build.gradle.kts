plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    implementation(libs.coroutines)
    implementation(libs.inject)
}