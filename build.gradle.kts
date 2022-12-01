import java.nio.file.Files
import java.nio.file.Path

plugins {
    java
    kotlin("jvm") version "1.8.0-Beta"
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
    kotlinOptions.freeCompilerArgs = listOf(
        "-Xuse-k2", // big boy compiler time
        "-opt-in=kotlin.time.ExperimentalTime", // KTIJ-22213 attempted fix
    )
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
    implementation("org.jetbrains:annotations:23.0.0")
    implementation(kotlin("reflect"))
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
