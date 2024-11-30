
import org.jetbrains.kotlin.gradle.internal.config.LanguageFeature

plugins {
    java
    // if kotlin is so good why isn't there a kotlin 2
    // oh, it's here
    kotlin("jvm") version "2.1.0"
    application
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
}

group = "sschr15"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("sschr15.aocsolutions.MainKt")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

detekt {
    buildUponDefaultConfig = false
    config.from("detekt-config.yml")
}

kotlin {
    compilerOptions {
        optIn.addAll(listOf( // opting in works around KTIJ-22253 and makes code a slight bit cleaner
            "kotlin.time.ExperimentalTime",
            "kotlin.ExperimentalStdlibApi",
            "kotlin.contracts.ExperimentalContracts",
            "kotlin.experimental.ExperimentalTypeInference",
        ))

        listOf(
            "NOTHING_TO_INLINE",
            "PropertyName",
            "NAME_SHADOWING",
        )
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
