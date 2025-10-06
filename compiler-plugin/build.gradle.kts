import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka") version "2.0.0"
    `maven-publish`
    signing
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
}

repositories {
    mavenCentral()
}

//dependencies {
//    compileOnly(kotlin("compiler"))
//}

kotlin {
    jvm()
    compilerOptions { 
        optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
    }

    sourceSets {
        jvmMain {
            dependencies {
                implementation(kotlin("stdlib"))
//                implementation(kotlin("compiler-embeddable"))
                compileOnly(kotlin("compiler"))
            }
        }
    }
}

//tasks.test {
//    useJUnitPlatform()
//}

val dokkaJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaGenerate)
}

val sourcesJar by tasks.getting

publishing {
    val publication by publications.registering(MavenPublication::class) {
        from(components["kotlin"])

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

allprojects {
    group = "com.sschr15.aoc"

    if (System.getenv("VERSION") != null) {
        version = System.getenv("VERSION")
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
}

rootProject.tasks.withType<KotlinCompile> {
    dependsOn(tasks.getByName("jvmJar"))
}
