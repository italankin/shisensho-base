package com.italankin.shisensho.game.util

import kotlin.math.abs

/**
 * @return manhattan distance between ([x1]; [y1]) and ([x2]; [y2])
 */
fun manhattan(x1: Int, y1: Int, x2: Int, y2: Int): Int {
    return abs(x2 - x1) + abs(y2 - y1)
}

/**
 * Converts a string like
 *
 * ```
 * . . . . . . .
 * . A . . . . .
 * . D . C G . .
 * . B G F E A .
 * . C B D E F .
 * . . . . . . .
 * ```
 *
 * into a list of [Char]s suitable for [ShisenShoGame] creation.
 *
 * @param s the string to parse
 * @param padded whether a string representation has padding ([nullChar]s around the field)
 * @param nullChar char for an empty cell
 *
 * @return puzzle width and tiles list
 */
fun tilesFromString(s: String, padded: Boolean, nullChar: Char = '.'): Pair<Int, List<Char?>> {
    val lines = ArrayList(s.lines())
    if (padded) {
        lines.removeFirst()
        lines.removeLast()
    }
    if (lines.isEmpty()) {
        throw IllegalArgumentException("Invalid input, no lines in string '$s' (padded=$padded)")
    }
    var width = 0
    val result = ArrayList<Char?>()
    for ((index, line) in lines.withIndex()) {
        val lineTiles = ArrayList<Char?>()
        var i = 0
        while (i < line.length) {
            val char = line[i]
            if (char == nullChar) {
                lineTiles.add(null)
            } else {
                lineTiles.add(char)
            }
            i += 2
        }
        if (padded) {
            lineTiles.removeFirst()
            lineTiles.removeLast()
        }
        if (width == 0) {
            width = lineTiles.size
        } else if (lineTiles.size != width) {
            throw IllegalArgumentException(
                "All lines must have $width tiles: " +
                        "found invalid line with ${lineTiles.size} tiles at index $index: '$line'"
            )
        }
        result.addAll(lineTiles)
    }
    return width to result
}
