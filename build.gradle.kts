import java.nio.file.Files
import java.nio.file.Path

plugins {
    kotlin("multiplatform") version "1.9.20"
}

group = "sschr15"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    linuxX64 {
//        kotlinOptions.freeCompilerArgs = listOf(
//            "-opt-in=kotlin.time.ExperimentalTime", // KTIJ-22213 attempted fix
//            "-opt-in=kotlin.ExperimentalStdlibApi", // because ..<
//        )
    }

    mingwX64 {
        binaries {
            executable()
        }
    }

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                implementation("com.sschr15.annotations:jb-annotations-kmp:24.0.1")
            }
        }
    }
}
