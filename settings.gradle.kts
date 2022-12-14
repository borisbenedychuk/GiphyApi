enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "GiphyApi"
include(":app")
include(":data")
include(":domain")
include(":core_ui")
include(":gifs_screen")
include(":benchmark")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    versionCatalogs.create("deps").from(files("deps.toml"))
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
