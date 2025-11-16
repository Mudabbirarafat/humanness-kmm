plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.20"
}

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.appcompat:appcompat:1.6.1")
            }
        }
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64().compilations["main"].defaultSourceSet.dependsOn(this)
            iosArm64().compilations["main"].defaultSourceSet.dependsOn(this)
            iosSimulatorArm64().compilations["main"].defaultSourceSet.dependsOn(this)
        }
    }
}

compose {
    kotlinCompilerPlugin.set(org.jetbrains.compose.ComposeCompilerPlugin.VERSION)
}
