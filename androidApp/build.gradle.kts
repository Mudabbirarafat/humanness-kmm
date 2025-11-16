plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose") // optional if using compose on android
}

android {
    namespace = "com.example.humanness"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.humanness"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.6.0" }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { jvmToolchain(17) }
}

repositories { google(); mavenCentral(); maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") }

dependencies {
    implementation(project(":shared"))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material:material:1.6.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
}
