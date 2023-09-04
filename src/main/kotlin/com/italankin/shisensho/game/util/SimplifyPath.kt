package com.italankin.shisensho.game.util

import com.italankin.shisensho.game.Path
import com.italankin.shisensho.game.Point
import kotlin.math.abs
import kotlin.math.min

/**
 * Simplifies (optimizes) path by removing intermediate points.
 *
 * For example, for a path like this:
 *
 * `[(1,1), (1,2), (1,3), (2,3), (3,3)]`
 *
 * result is:
 *
 * `[(1,1), (1,3), (3,3)]`.
 *
 * For a path with `S` segments the resulting array will contain `S + 1` elements.
 */
fun Path.simplify(): Path {
    if (size < 2) {
        return this
    }
    val result = ArrayList<Point>(4)
    result.add(first())
    var prev = first()
    var prevDir = 0
    for (i in 1..lastIndex) {
        val cur = get(i)
        // any difference in x will result in +1
        // any difference in y will result in +2
        // resulting direction will be:
        // - 0 for no movement (two points are the same)
        // - 1 for horizontal movement
        // - 2 for vertical movement
        // - 3 for diagonal movement
        val dir = min(1, abs(prev.x - cur.x)) + 2 * min(1, abs(prev.y - cur.y))
        if (dir == 0) {
            // duplicate points in path, just skip
            continue
        } else if (dir == 3) {
            throw IllegalStateException("Found impossible segment: ${prev}, $cur")
        }
        if (prevDir == 0) {
            prevDir = dir
        } else if (prevDir != dir) {
            result.add(prev)
            prevDir = dir
        }
        prev = cur
    }
    result.add(last())
    return result
}
