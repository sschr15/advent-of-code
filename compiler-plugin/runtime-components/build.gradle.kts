plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    linuxX64()
    linuxArm64()
    macosX64()
    macosArm64()

    sourceSets.commonMain {
        dependencies {
            implementation(kotlin("stdlib"))
        }
    }
}

val dokkaJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaGenerate)
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                name = "AoC Utility Kotlin Runtime Components"
                description =
                    "Contains the code the compiler plugin compiles against. The name is a misnomer; it is only needed at compile time."

                url = "https://github.com/sschr15/aoc-solutions"

                developers {
                    developer {
                        name = "sschr15"
                        email = "me@sschr15.com"
                        url = "https://sschr15.com"
                        timezone = "America/Chicago"
                    }
                }

                scm {
                    connection = this@pom.url.get().replace("https", "scm:git:git")
                    developerConnection = connection.get().replace("git://", "ssh://")
                    url = this@pom.url
                }
            }

            artifact(dokkaJar) {
                classifier = "javadoc"
            }
        }

        val jvm by getting(MavenPublication::class) {
            artifact(tasks.getByName("jvmSourcesJar")) {
                classifier = "sources"
            }
        }

        val linuxX64 by getting(MavenPublication::class) {
            artifact(tasks.getByName("linuxX64SourcesJar")) {
                classifier = "sources"
            }
        }

        val linuxArm64 by getting(MavenPublication::class) {
            artifact(tasks.getByName("linuxArm64SourcesJar")) {
                classifier = "sources"
            }
        }

        // Don't attempt to set up macos publications on non-macos systems
        if (findByName("macosX64") == null) return@publications

        val macosX64 by getting(MavenPublication::class) {
            artifact(tasks.getByName("macosX64SourcesJar")) {
                classifier = "sources"
            }
        }

        val macosArm64 by getting(MavenPublication::class) {
            artifact(tasks.getByName("macosArm64SourcesJar")) {
                classifier = "sources"
            }
        }
    }
}
