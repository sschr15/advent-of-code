@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    // if kotlin is so good why isn't there a kotlin 2
    // oh, it's here
    kotlin("multiplatform") version "2.2.20"
    kotlin("plugin.power-assert") version "2.2.20"
}

group = "sschr15"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

powerAssert {
    functions = listOf("kotlin.assert", "kotlin.require", "kotlin.check")
    includedSourceSets = listOf("commonMain", "jvmMain")
}

kotlin {
    jvm()
    jvmToolchain(21)

    linuxX64 {
        binaries {
            executable {
                entryPoint = "sschr15.aocsolutions.main"
            }
        }

        // Avoid cinterop setup on CI stuff (the things that upload the compiler plugin)
        if (!System.getenv("CI").isNullOrBlank()) return@linuxX64

        compilations["main"].cinterops { 
            val igraph by creating {
                defFile(file("src/linuxX64Main/cinterop/igraph.def"))
                includeDirs.allHeaders("/usr/include", "/usr/include/igraph")
            }
        }

        compilerOptions {
            optIn.addAll(
                "kotlinx.cinterop.ExperimentalForeignApi",
            )
        }
    }

    compilerOptions {
        optIn.addAll(listOf( // opting in works around KTIJ-22253 and makes code a slight bit cleaner
            "kotlin.time.ExperimentalTime",
            "kotlin.ExperimentalStdlibApi",
            "kotlin.contracts.ExperimentalContracts",
            "kotlin.experimental.ExperimentalTypeInference",
            "kotlin.concurrent.atomics.ExperimentalAtomicApi",
        ))

        freeCompilerArgs.addAll(
            "-Xcontext-parameters",
            "-Xwhen-guards",
            "-Xexpect-actual-classes",
        )

        freeCompilerArgs.addAll(listOf(
            "NOTHING_TO_INLINE",
        ).map { "-Xwarning-level=$it:disabled" })

        freeCompilerArgs.add("-Xplugin=${project(":compiler-plugin").file("build/libs/compiler-plugin-jvm.jar")}")
    }

//    sourceSets.configureEach { 
//        languageSettings { 
//            listOf(
//                LanguageFeature.ContextReceivers, // for my z3 wrapper
//                LanguageFeature.BreakContinueInInlineLambdas, // because very useful
//                LanguageFeature.WhenGuards, // copying java's "new" switch guards
//                LanguageFeature.MultiDollarInterpolation, // just in case
//            ).forEach { enableLanguageFeature(it.toString()) }
//        }
//    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains:annotations:26.0.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
                implementation("com.fleeksoft.ksoup:ksoup:0.2.4")
                implementation("io.ktor:ktor-client-core:3.2.2")
                implementation("com.sschr15.z3kt:z3kt:0.7.0")

                implementation(project(":compiler-plugin:runtime-components"))
            }
        }
        jvmMain {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("reflect"))
                implementation("org.jgrapht:jgrapht-core:1.5.2")
                implementation("it.unimi.dsi:fastutil:8.5.12")
                implementation("io.ktor:ktor-client-java:3.2.2")
                implementation(files("com.microsoft.z3.jar"))
            }
        }
        linuxX64Main {
            dependencies {
                implementation("io.ktor:ktor-client-curl:3.2.2")
            }
        }
    }
}

dependencies {
//    listOf(
//        "stdlib",
//        "stdlib-common",
//        "stdlib-jdk8",
//        "stdlib-jdk7",
//        "reflect"
//    ).forEach {
//        implementation(kotlin(it))
//    }
//    implementation("org.jetbrains:annotations:26.0.1")
//    implementation("com.sschr15:templates-kt:1.0.0") // here because i want java 21 string templates
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
//    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
//    implementation("it.unimi.dsi:fastutil:8.5.12")
//    implementation("org.jgrapht:jgrapht-core:1.5.2")
//    implementation("org.jsoup:jsoup:1.15.3")
//
//    implementation(files("z3/kotlin-z3-wrapper.jar"))
//
//    implementation(projects.compilerPlugin.runtimeComponents)
}

//tasks {
//    compileKotlin {
//        dependsOn(project(":compiler-plugin").tasks.jar)
//        // force recompile
////        outputs.upToDateWhen { false }
////        outputs.cacheIf { false }
//    }
//
//    val benchmark by registering(JavaExec::class) {
//        group = "application"
//        description = "Run SolutionTimer, benchmarking every solution"
//        mainClass = "sschr15.aocsolutions.SolutionTimer"
//        classpath = sourceSets["main"].runtimeClasspath
//    }
//}
