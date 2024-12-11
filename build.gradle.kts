@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.internal.config.LanguageFeature

plugins {
    java
    // if kotlin is so good why isn't there a kotlin 2
    // oh, it's here
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.power-assert") version "2.1.0"
    application
}

group = "sschr15"
version = "1.0-SNAPSHOT"

application {
    mainClass = "sschr15.aocsolutions.MainKt"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

powerAssert {
    functions = listOf("kotlin.assert", "kotlin.require", "kotlin.check")
    includedSourceSets = listOf("main")
}

kotlin {
    compilerOptions {
        optIn.addAll(listOf( // opting in works around KTIJ-22253 and makes code a slight bit cleaner
            "kotlin.time.ExperimentalTime",
            "kotlin.ExperimentalStdlibApi",
            "kotlin.contracts.ExperimentalContracts",
            "kotlin.experimental.ExperimentalTypeInference",
        ))

        freeCompilerArgs.addAll(listOf(
            "NOTHING_TO_INLINE",
        ).map { "-Xsuppress-warning=$it" })
    }

    sourceSets.configureEach { 
        languageSettings { 
            listOf(
                LanguageFeature.ContextReceivers, // for my z3 wrapper
                LanguageFeature.BreakContinueInInlineLambdas, // because very useful
                LanguageFeature.WhenGuards, // copying java's "new" switch guards
                LanguageFeature.MultiDollarInterpolation, // just in case
            ).forEach { enableLanguageFeature(it.toString()) }
        }
    }
}

dependencies {
    listOf(
        "stdlib",
        "stdlib-common",
        "stdlib-jdk8",
        "stdlib-jdk7",
        "reflect"
    ).forEach {
        implementation(kotlin(it))
    }
    implementation("org.jetbrains:annotations:26.0.1")
    implementation("com.sschr15:templates-kt:1.0.0") // here because i want java 21 string templates
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("it.unimi.dsi:fastutil:8.5.12")
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    implementation("org.jsoup:jsoup:1.15.3")

    implementation(files("kotlin-z3-bindings.jar"))
}

tasks {
    val benchmark by registering(JavaExec::class) {
        group = "application"
        description = "Run SolutionTimer, benchmarking every solution"
        mainClass = "sschr15.aocsolutions.SolutionTimer"
        classpath = sourceSets["main"].runtimeClasspath
    }
}
