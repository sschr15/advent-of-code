plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.9.20"
    `maven-publish`
    signing
    java
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "java")
}

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

allprojects {
    group = "com.sschr15.aoc"

    if (System.getenv("VERSION") != null) {
        version = System.getenv("VERSION")
    }

    val dokkaJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
        from(tasks.dokkaHtml)
    }

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.getByName("main").allSource)
    }

    signing {
        val signingKey = System.getenv("SIGNING_KEY")
        val signingPassword = System.getenv("SIGNING_PASSWORD")
        if (signingKey != null && signingPassword != null) {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publishing.publications)
        }
    }

    tasks.withType<PublishToMavenRepository> {
        mustRunAfter(tasks.withType<Sign>())
    }

    publishing {
        val publication by publications.registering(MavenPublication::class) {
            from(components["java"])

            artifact(dokkaJar) {
                classifier = "javadoc"
            }

            artifact(sourcesJar) {
                classifier = "sources"
            }

            pom {
                licenses { 
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                name = "AoC Utility Kotlin Compiler Plugin"
                description = "Adds some utilities to code, developed originally for Advent of Code solving"
                url = "https://github.com/sschr15/advent-of-code/"

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
        }

        repositories { 
            if (System.getenv("MAVEN_URL") != null) {
                maven(System.getenv("MAVEN_URL")) {
                    if (System.getenv("MAVEN_USERNAME") != null) {
                        credentials {
                            username = System.getenv("MAVEN_USERNAME")
                            password = System.getenv("MAVEN_PASSWORD")
                        }
                    }
                }
            }
        }
    }
}
