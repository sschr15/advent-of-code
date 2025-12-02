@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.internal.config.LanguageFeature

plugins {
    java
    // if kotlin is so good why isn't there a kotlin 2
    // oh, it's here
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.power-assert") version "2.2.20"
    id("com.sschr15.chekt") version "0.2.0"
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
    mavenLocal()
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

        freeCompilerArgs.addAll(
            "-Xwhen-guards", // When guards: switch guards for kotlin
            "-Xcontext-parameters", // the newer version of context receivers
            "-Xnested-type-aliases", // type aliases meet inception
            "-Xallow-reified-type-in-catch", // catching reified exceptions is a thing now
            "-Xallow-contracts-on-more-functions", // operators, accessors, and "erased types" (???)
            "-Xallow-condition-implies-returns-contracts", // (condition) implies returns()
            "-Xallow-holdsin-contract", // a condition that "holds in" a lambda block
            "-Xcontext-sensitive-resolution", // enums can be referenced by names with a `when` enum condition, and similar
            "-Xnon-local-break-continue", // `break` and `continue` in inline lambdas
            "-Xmulti-dollar-interpolation", // prefixing a string with dollar signs to change the interpolation signifier
//            "-Xdirect-java-actualization", // for multiplatform, actual classes implemented in java
            "-Xwhen-expressions=indy", // compile `when` expressions on types into invokedynamic (java 21+)

            "-Xwarning-level=NOTHING_TO_INLINE:disabled",
        )
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
    implementation("org.jetbrains:annotations:26.0.2-1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
    implementation("it.unimi.dsi:fastutil:8.5.18")
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.sschr15.z3kt:z3kt-jvm:0.6.0")
    runtimeOnly(files("com.microsoft.z3.jar"))
}

tasks {
    compileKotlin {
        // force recompile
//        outputs.upToDateWhen { false }
//        outputs.cacheIf { false }
    }

    val benchmark by registering(JavaExec::class) {
        group = "application"
        description = "Run SolutionTimer, benchmarking every solution"
        mainClass = "sschr15.aocsolutions.SolutionTimer"
        classpath = sourceSets["main"].runtimeClasspath
    }
}
