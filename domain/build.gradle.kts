plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    implementation(libs.coroutines)
    implementation(libs.inject)
    testImplementation(libs.bundles.unitTesting)
    testImplementation(libs.coroutinesTest)
}