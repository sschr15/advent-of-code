# advent-of-code

This is a repository containing my solutions to the 
[Advent of Code](https://adventofcode.com/about) challenges as well as a
[compiler plugin](compiler-plugin) providing additional features for
ease of solving.

## Getting challenge inputs

Puzzle inputs can be provided in two ways:
1. If a file named `session.txt` is provided in the run directory
   (the root of the repository if run via Gradle), each challenge
   will be downloaded using the session token provided in plain text
   within that file.
2. Inputs can be manually placed into `inputs/YEAR/dayN` files, given
   `YEAR` being the current four-digit year and `N` being a number 1 through 25.
   The file may have a `.txt` extension, but it is not required.

## Running all solutions

1. Install Java 21 or later and ensure it is the default version.
2. Run one of the gradle tasks:
   1. `./gradlew run` - Runs all solutions in parallel, printing the results in order.
   2. `./gradlew benchmark` - Runs each solution at least 250 times or at most five minutes,
      for five seconds (or longer if the solution takes longer) and prints the average,
      minimum, and maximum execution times as well as the standard deviation.
      To best benchmark the multithreaded solutions, each is run on its own.

If any exceptions are thrown, the program will catch them, print the error message,
and export the stack trace to a file named `dayN_error.txt`.
It will then continue to the next solution.

## What's [`compiler-plugin`](compiler-plugin)?

This is a custom plugin providing an annotation for adding memoization
to functions as well as automatically checking for integer/long overflow.
To use it, add the following to a project's `build.gradle.kts`:

```kotlin
val compilerPlugin by configurations.creating

dependencies {
    implementation("com.sschr15.aoc:runtime-components:0.1.0")
    compilerPlugin("com.sschr15.aoc:compiler-plugin:0.1.0")
}

kotlin {
    compilerOptions {
        for (plugin in compilerPlugin) {
            freeCompilerArgs.add("-Xplugin=${plugin.absolutePath}")
        }
    }
}
```

I plan on providing a Gradle plugin at some point to avoid the need for manual configuration.
