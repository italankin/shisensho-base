package com.italankin.shisensho.game.impl

import com.italankin.shisensho.game.Path
import com.italankin.shisensho.game.Point
import com.italankin.shisensho.game.ShisenShoGame
import com.italankin.shisensho.game.Tile

/**
 * Implementation of [ShisenShoGameImpl.PathFinder] which uses simple iterative search
 */
object SimplePathFinder : ShisenShoGameImpl.PathFinder {

    private val DIRECTIONS = arrayOf(
        -1 to 0, // left
        1 to 0, // right
        0 to -1, // top
        0 to 1 // bottom
    )

    override fun findPath(game: ShisenShoGame, a: Tile, b: Tile): Path {
        val simplePath = game.findSimplePath(a.x, a.y, b.x, b.y)
        if (simplePath.isNotEmpty()) {
            return simplePath
        }
        return game.findThreeSegmentsPath(a, b)
    }

    /**
     * Find a simple (2 segments) path from `(x1, y1)` to `(x2, y2)`
     */
    private fun ShisenShoGame.findSimplePath(x1: Int, y1: Int, x2: Int, y2: Int): List<Point> {
        val simplePath1 = findSimplePathXY(x1, y1, x2, y2)
        if (simplePath1.isNotEmpty()) {
            return simplePath1
        }
        val simplePath2 = findSimplePathYX(x1, y1, x2, y2)
        if (simplePath2.isNotEmpty()) {
            return simplePath2
        }
        return emptyList()
    }

    /**
     * Find a path by moving on the X axis first.
     *
     * ```
     *     (x1,y1) ----> (x2,y1)
     *                      |
     *                      |
     *                      v
     *                   (x2,y2)
     * ```
     */
    private fun ShisenShoGame.findSimplePathXY(x1: Int, y1: Int, x2: Int, y2: Int): List<Point> {
        val path = ArrayList<Point>(8)
        for (x in x1 inclInclRangeTo x2) {
            if (get(x, y1) == null) {
                path.add(Point(x, y1))
            } else {
                return emptyList()
            }
        }
        for (y in y1 exclInclRangeTo y2) {
            if (y == y2 || get(x2, y) == null) {
                path.add(Point(x2, y))
            } else {
                return emptyList()
            }
        }
        return path
    }

    /**
     * Find a path by moving on the Y axis first.
     *
     * ```
     *     (x1,y1)
     *        |
     *        |
     *        v
     *     (x1,y2) ----> (x2,y2)
     * ```
     */
    private fun ShisenShoGame.findSimplePathYX(x1: Int, y1: Int, x2: Int, y2: Int): List<Point> {
        val path = ArrayList<Point>(8)
        for (y in y1 inclInclRangeTo y2) {
            if (get(x1, y) == null) {
                path.add(Point(x1, y))
            } else {
                return emptyList()
            }
        }
        for (x in x1 exclInclRangeTo x2) {
            if (x == x2 || get(x, y2) == null) {
                path.add(Point(x, y2))
            } else {
                return emptyList()
            }
        }
        return path
    }

    /**
     * `[this, to]`
     */
    private infix fun Int.inclInclRangeTo(to: Int): Iterable<Int> {
        return when {
            this > to -> IntProgression.fromClosedRange(this, to, -1)
            else -> this..to
        }
    }

    /**
     * `(this, to]`
     */
    private infix fun Int.exclInclRangeTo(to: Int): Iterable<Int> {
        return when {
            this > to -> IntProgression.fromClosedRange(this - 1, to, -1)
            else -> (this + 1)..to
        }
    }

    private fun ShisenShoGame.findThreeSegmentsPath(a: Tile, b: Tile): List<Point> {
        for ((dx, dy) in DIRECTIONS) {
            val path = ArrayList<Point>(8)
            var cx = a.x
            var cy = a.y
            while (cx in minX..maxX && cy in minY..maxY) {
                if (cx == a.x && cy == a.y || cx == b.x && cy == b.y) {
                    path.add(Point(cx, cy))
                } else if (get(cx, cy) == null) {
                    val simplePathSegment = findSimplePath(cx, cy, b.x, b.y)
                    if (simplePathSegment.isNotEmpty()) {
                        path.addAll(simplePathSegment)
                        return path
                    } else {
                        path.add(Point(cx, cy))
                    }
                } else {
                    // no path can be found in this direction, proceed to next
                    break
                }
                cx += dx
                cy += dy
            }
        }
        return emptyList()
    }
}
