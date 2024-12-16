# AdventOfCode

This is a repository containing *my personal solutions* to the 
[Advent of Code](https://adventofcode.com/2021/about) challenges. All challenge files must be
downloaded from the Advent of Code website and to be used without modification must be named as `dayN` in
`inputs/<year>/` (where `N` is which day of 25 we are at, with a variable digit count).

## Running all solutions
1. Make sure you have installed Java 21 or higher, and that gradle is directed to use Java 21.
2. Place all solutions in the `inputs/<year>/` directory, as files named as `dayN` without an extension
   or with the `.txt` extension.
3. Run `./gradlew run` to run all solutions.

If any exceptions are thrown, the program will catch them, print the error message,
and export the stacktrace to a `dayN_error.txt` file. It will then continue to the next solution.

## What's `compiler-plugin`?

This is a custom plugin providing an annotation for adding memoization to functions as well as automatically
checking for integer/long overflow. If you want to use it, you can add the following to your `build.gradle.kts`:

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
