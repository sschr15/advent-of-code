plugins {
    java
    kotlin("jvm") version "1.6.0"
    application
}

group = "sschr15"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("sschr15.aocsolutions.MainKt")
}

java {
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs = listOf("-opt-in=kotlin.time.ExperimentalTime")
}

dependencies {
    listOf(
        "stdlib",
        "stdlib-common",
        "stdlib-jdk8",
        "stdlib-jdk7",
    ).forEach {
        implementation(kotlin(it))
    }
}
