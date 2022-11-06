@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    `java-library`
    alias { deps.plugins.kotlinJvm }
}

dependencies {
    implementation(deps.coroutines)
    implementation(deps.inject)
    testImplementation(deps.bundles.unitTesting)
}