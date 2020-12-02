package sschr15.aocsolutions

import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path

/**
 * Get a challenge's file
 * @param year the year of the challenge, or `0` for the part 3 challenge
 * @param day the day of the challenge, according to the Advent of Code website.
 * @return a [BufferedReader] pointing to the challenge's file
 */
fun getChallenge(year: Int, day: Int): BufferedReader =
    Files.newBufferedReader(Path.of(if (year == 0) "part3" else "inputs/$year", "day$day"))
