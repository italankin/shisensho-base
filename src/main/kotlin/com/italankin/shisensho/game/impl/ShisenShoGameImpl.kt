package com.italankin.shisensho.game.impl

import com.italankin.shisensho.game.ChineseStyleTileValueMatcher
import com.italankin.shisensho.game.EqTileValueMatcher
import com.italankin.shisensho.game.Move
import com.italankin.shisensho.game.Path
import com.italankin.shisensho.game.Point
import com.italankin.shisensho.game.ShisenShoGame
import com.italankin.shisensho.game.ShisenShoGameGenerator
import com.italankin.shisensho.game.Tile
import com.italankin.shisensho.game.TileValue
import com.italankin.shisensho.game.TileValueMatcher
import com.italankin.shisensho.game.gameHeight
import com.italankin.shisensho.game.gameWidth
import kotlin.random.Random

/**
 * Basic implementation of [ShisenShoGame].
 *
 * @param tiles list of [TileValue]s which will populate field (by rows)
 * @param width width of a row
 * @param gravity whether gravity is enabled
 * @param pathFinder algorithm for finding complex paths
 * @param tileValueMatcher matcher for tile values (either [EqTileValueMatcher] or [ChineseStyleTileValueMatcher])
 */
class ShisenShoGameImpl(
    tiles: List<TileValue?>,
    width: Int,
    override val gravity: Boolean,
    private val pathFinder: PathFinder,
    private val tileValueMatcher: TileValueMatcher
) : ShisenShoGame {

    private val tilesArray: Array<Array<Tile?>>
    private var tilesRemaining: Int = 0
    private val history: ArrayDeque<Array<Tile?>> = ArrayDeque(tiles.size / 2 - 1)
    private val tileKeySelector: (Tile) -> TileValue =
        if (tileValueMatcher is ChineseStyleTileValueMatcher) {
            ChineseStyleKeySelector()
        } else {
            Tile::value
        }

    private var cachedPossibleMoves: List<Move>? = null

    init {
        if (tiles.size % width != 0) {
            throw IllegalArgumentException()
        }
        val height = tiles.size / width
        tilesArray = Array(width + 2) { Array(height + 2) { null } }
        var index = 0
        var count = 0
        for (y in 1..height) {
            for (x in 1..width) {
                val c = tiles[index++]
                if (c != null) {
                    val tile = Tile(x = x, y = y, value = c)
                    tile.tileValueMatcher = tileValueMatcher
                    tilesArray[x][y] = tile
                    count++
                }
            }
        }
        tilesRemaining = count
        if (gravity) {
            applyGravity(null)
        }
    }

    override val tiles: List<Tile>
        get() = tilesAsList()

    override val remaining: Int
        get() = tilesRemaining

    override val total: Int = tiles.size

    override val seed: Int by lazy { System.identityHashCode(this) }

    override val minX: Int = 0

    override val minY: Int = 0

    override val maxX: Int = width + 1

    override val maxY: Int = (tiles.size / width) + 1

    override fun move(a: Tile, b: Tile, movements: MutableList<Tile>?): List<Point> {
        val path = findPathInternal(a, b)
        if (path.isNotEmpty()) {
            cachedPossibleMoves = null
            historyPush()
            tilesArray[a.x][a.y] = null
            tilesArray[b.x][b.y] = null
            tilesRemaining -= 2
            if (gravity) {
                applyGravity(movements)
            }
        }
        return path
    }

    override fun canMove(a: Tile, b: Tile): Path {
        val cached = cachedPossibleMoves
        if (cached != null) {
            return cached.firstOrNull { move -> a in move && b in move }
                ?.path
                ?: emptyList()
        }
        return findPathInternal(a, b)
    }

    override fun hasMoves(): Boolean {
        val cached = cachedPossibleMoves
        if (cached != null) {
            return cached.isNotEmpty()
        }
        return possibleMoves().isNotEmpty()
    }

    override fun canUndo(): Boolean {
        return history.isNotEmpty()
    }

    override fun undo() {
        if (history.isEmpty()) {
            return
        }
        val entry = history.removeLast()
        tilesRemaining = 0
        val width = maxX + 1
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                val tile = entry[width * y + x]
                if (tile != null) {
                    tilesArray[x][y] = tile
                    tilesRemaining++
                } else {
                    tilesArray[x][y] = null
                }
            }
        }
        cachedPossibleMoves = null
    }

    override fun get(x: Int, y: Int): Tile? {
        return tilesArray[x][y]
    }

    override fun possibleMoves(): List<Move> {
        val cached = cachedPossibleMoves
        if (cached != null) {
            return cached
        }
        val possibleMoves = findPossibleMoves()
        cachedPossibleMoves = possibleMoves
        return possibleMoves
    }

    override fun possibleMoves(start: Tile): List<Move> {
        return possibleMoves().filter { move -> start in move }
    }

    override fun shuffle(hasMoves: Boolean, preserveLayout: Boolean, random: Random) {
        if (tilesRemaining == 0) {
            return
        }
        historyPush()
        do {
            cachedPossibleMoves = null
            if (preserveLayout) {
                shuffleInPlace(random)
            } else {
                shuffle(random)
            }
        } while (hasMoves && !hasMoves())
    }

    override fun copy(): ShisenShoGame {
        val tiles = ArrayList<TileValue?>(gameWidth * gameHeight)
        for (y in 1 until maxY) {
            for (x in 1 until maxX) {
                tiles += get(x, y)?.value
            }
        }
        return ShisenShoGameImpl(tiles, gameWidth, gravity, pathFinder, tileValueMatcher)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                val t = tilesArray[x][y]
                val s = if (t != null) "${t.value.c} " else ". "
                sb.append(s)
            }
            sb.append('\n')
        }
        return sb.toString()
    }

    private fun tilesAsList(): ArrayList<Tile> {
        val tiles = ArrayList<Tile>(tilesRemaining)
        for (col in tilesArray) {
            for (tile in col) {
                if (tile != null) {
                    tiles.add(tile)
                }
            }
        }
        return tiles
    }

    private fun shuffleInPlace(random: Random) {
        val tiles = tilesAsList()
        tiles.shuffle(random)
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                if (tilesArray[x][y] != null) {
                    val tile = tiles.removeFirst()
                    tile.x = x
                    tile.y = y
                    tilesArray[x][y] = tile
                }
            }
        }
    }

    private fun shuffle(random: Random) {
        val tiles = ArrayList<Tile?>((maxX - 1) * (maxY - 1))
        for (x in 1 until maxX) {
            for (y in 1 until maxY) {
                tiles.add(tilesArray[x][y])
            }
        }
        tiles.shuffle(random)
        for (x in 1 until maxX) {
            for (y in 1 until maxY) {
                val tile = tiles.removeFirst()
                if (tile != null) {
                    tile.x = x
                    tile.y = y
                }
                tilesArray[x][y] = tile
            }
        }
        if (gravity) {
            applyGravity(null)
        }
    }

    private fun historyPush() {
        val width = maxX + 1
        val state = Array<Tile?>((maxX + 1) * (maxY + 1)) { null }
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                state[y * width + x] = tilesArray[x][y]?.copy()
            }
        }
        history.add(state)
    }

    /**
     * Algorithm iterates over tile columns and for each column:
     * - set `pointer` to bottom position
     * - from bottom position go up until we find a tile at `(x;y)`
     * - set `(x;pointer)` as new position for tile and move pointer up
     *
     * @param movements optional list where tile movements will be written
     */
    private fun applyGravity(movements: MutableList<Tile>?) {
        for (col in tilesArray) {
            var pointer = col.size - 2
            for (y in (col.size - 1) downTo 1) {
                val tile = col[y]
                if (tile != null) {
                    val newY = pointer--
                    if (tile.y != newY) {
                        tile.y = newY
                        col[newY] = tile
                        if (movements != null) {
                            movements += tile
                        }
                    }
                }
            }
            while (pointer > 0) {
                col[pointer--] = null
            }
        }
    }

    private fun findPossibleMoves(): List<Move> {
        if (tilesRemaining == 0) {
            return emptyList()
        }
        val moves = ArrayList<Move>(8)
        val tiles = tilesAsList()
        val byValue = HashMap<TileValue, MutableList<Tile>>(tiles.size)
        for (tile in tiles) {
            val key = tileKeySelector(tile)
            val list = byValue.getOrPut(key) { ArrayList(4) }
            list.add(tile)
        }
        byValue.forEach { (_, value) ->
            while (value.size > 1) {
                val a = value.removeFirst()
                for (b in value) {
                    val path = findPathInternal(a, b)
                    if (path.isNotEmpty()) {
                        moves.add(Move(a, b, path))
                    }
                }
            }
        }
        return moves
    }

    /**
     * Checks whether tile has at least one empty cell nearby
     */
    private fun Tile.isFree(): Boolean {
        return x - 1 >= minX && tilesArray[x - 1][y] == null ||
                x + 1 <= maxX && tilesArray[x + 1][y] == null ||
                y - 1 >= minY && tilesArray[x][y - 1] == null ||
                y + 1 <= maxY && tilesArray[x][y + 1] == null
    }

    /**
     * Find a path between [a] and [b], according to shisen sho rules
     *
     * @return a list of points which form a path from [b] to [a], or an empty list if there's none
     */
    private fun findPathInternal(a: Tile, b: Tile): List<Point> {
        // do simple checks
        if (a.layer != b.layer) {
            return emptyList()
        }
        if (!a.valueMatches(b)) {
            // not matching tile
            return emptyList()
        }
        if (a.x == b.x && a.y == b.y) {
            // the same tile
            return emptyList()
        }
        if (a.distanceTo(b) == 1) {
            // tiles stand next to each other
            return listOf(Point(a.x, a.y), Point(b.x, b.y))
        }
        if (!a.isFree() || !b.isFree()) {
            // one of the tiles are surrounded by other tiles
            return emptyList()
        }
        // run full search
        return pathFinder.findPath(this, a, b)
    }

    interface PathFinder {

        /**
         * Find a path between [a] and [b].
         *
         * Basic checks are performed by [ShisenShoGameImpl], e.g. whether tiles have at least one
         * empty cell nearby.
         *
         * @return a path from [a] to [b] or empty list if there's no path
         */
        fun findPath(game: ShisenShoGame, a: Tile, b: Tile): Path
    }

    /**
     * [ShisenShoGameGenerator.GameFactory] for games with [BfsPathFinder] algorithm
     */
    object BfsFactory : ShisenShoGameGenerator.GameFactory {

        override fun create(
            tiles: List<TileValue?>,
            width: Int,
            gravity: Boolean,
            tileValueMatcher: TileValueMatcher
        ): ShisenShoGame {
            return ShisenShoGameImpl(tiles, width, gravity, BfsPathFinder, tileValueMatcher)
        }

        override fun toString(): String = "BfsFactory"
    }

    /**
     * [ShisenShoGameGenerator.GameFactory] for games with [SimplePathFinder] algorithm
     */
    object SimpleFactory : ShisenShoGameGenerator.GameFactory {

        override fun create(
            tiles: List<TileValue?>,
            width: Int,
            gravity: Boolean,
            tileValueMatcher: TileValueMatcher
        ): ShisenShoGame {
            return ShisenShoGameImpl(tiles, width, gravity, SimplePathFinder, tileValueMatcher)
        }

        override fun toString(): String = "SimpleFactory"
    }

    private class ChineseStyleKeySelector : (Tile) -> TileValue {

        override fun invoke(tile: Tile): TileValue {
            // group tiles from flowers and season sets by single value
            return when (tile.value) {
                in TileValue.FLOWERS_SET -> TileValue.FLOWER_PLUM
                in TileValue.SEASONS_SET -> TileValue.SEASON_SPRING
                else -> tile.value
            }
        }
    }
}

