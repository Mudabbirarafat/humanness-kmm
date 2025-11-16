pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    plugins {
        id("com.android.application") version "8.1.1"
        kotlin("multiplatform") version "1.9.20"
        kotlin("android") version "1.9.20"
        id("org.jetbrains.compose") version "1.5.1"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
rootProject.name = "humanness-kmm-compose"
include(":androidApp")
include(":shared")
include(":iosApp")
