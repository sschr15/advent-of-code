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

## License

I am licensing this under the MIT license even though I suggest people not use my solutions. I'd still
consider myself not the best when it comes to programming in general, but I have felt that I've improved.
~~However, [GeneralUtils](src/main/kotlin/sschr15/aocsolutions/GeneralUtils.kt) might actually have some
good code.~~ Nothing ever is good code if I touch it.

Proceed with caution!
