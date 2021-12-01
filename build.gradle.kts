plugins {
    java
    kotlin("jvm") version "1.6.0"
}

group = "sschr15"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

dependencies {
    implementation(kotlin("stdlib"))
}
