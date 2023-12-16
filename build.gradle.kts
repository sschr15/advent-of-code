import java.nio.file.Files
import java.nio.file.Path

plugins {
    java
    kotlin("jvm") version "1.9.21"
    application
}

group = "sschr15"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("sschr15.aocsolutions.MainKt")
}

java {
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
    kotlinOptions.freeCompilerArgs = listOf(
        "-opt-in=kotlin.time.ExperimentalTime", // KTIJ-22253 attempted fix
        "-opt-in=kotlin.ExperimentalStdlibApi", // because previously needed
        "-opt-in=kotlin.contracts.ExperimentalContracts", // contracts are cool, regardless of how experimental they are (also KTIJ-22253)
        "-opt-in=kotlin.experimental.ExperimentalTypeInference", // because OverloadResolutionByLambdaReturnType
    )
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
    implementation("com.sschr15.annotations:jb-annotations-kmp:24.0.1")
    implementation("com.sschr15:templates-kt:1.0.0") // here because i want java 21 string templates
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}

afterEvaluate {
    fun path(name: String) = project.file(name).toPath()

    Files.createDirectories(path("run"))
    val isWin = "win" in System.getProperty("os.name").toLowerCase()
    val pythonExecutable = if (isWin) "python.exe" else "python3"
    val py3Exists = System.getenv("PATH").split(if (isWin) ";" else ":")
        .map { Path.of(it, pythonExecutable) }
        .filter { Files.exists(it) }
        .map {
            val process = ProcessBuilder(it.toString(), "--version").start()
            process.waitFor()
            process.inputStream.bufferedReader().readLine()
        }
        .any { it.startsWith("Python 3") }

    if (!py3Exists) {
        logger.error("Python 3 is not installed! Some challenges may not be solvable.")
    }
}
