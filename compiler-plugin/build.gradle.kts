plugins {
    kotlin("jvm")
}

group = "sschr15"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("compiler"))
}

kotlin {
    compilerOptions { 
        optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
    }
}

tasks.test {
    useJUnitPlatform()
}
