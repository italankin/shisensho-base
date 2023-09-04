package com.italankin.shisensho.game

import kotlin.random.Random

private const val HISTORY_SHUFFLE = -1

/**
 * [ShisenShoGame] with support of multiple game layers.
 */
interface MultiLayerShisenShoGame : ShisenShoGame {

    val layersSize: Int

    /**
     * @return layer at [index], where `0` is the top.
     * The returned game is read-only and does not provide information about possible
     * moves or undo state.
     *
     * @see [layersSize]
     */
    fun layer(index: Int): ShisenShoGame
}

class MultiLayerShisenShoGameImpl(
    private vararg val layers: ShisenShoGame
) : MultiLayerShisenShoGame {

    // store layer indices as states and special HISTORY_SHUFFLE for shuffles
    private val history: ArrayDeque<Int> = ArrayDeque()
    private var cachedPossibleMoves: List<Move>? = null

    init {
        if (layers.isEmpty()) {
            throw IllegalArgumentException("layers must not be empty")
        }
        val top = layers[0]
        if (top is MultiLayerShisenShoGame) {
            throw IllegalArgumentException("Cannot nest `MultiLayerShisenShoGame`s")
        }
        // check layer configuration
        for (i in 1 until layers.size) {
            val game = layers[i]
            if (game is MultiLayerShisenShoGame) {
                throw IllegalArgumentException("Cannot nest `MultiLayerShisenShoGame`s")
            }
            if (top.gravity != game.gravity) {
                throw IllegalArgumentException("gravity mismatch at index $i: ${top.gravity} != ${game.gravity}")
            }
            if (top.minX != game.minX) {
                throw IllegalArgumentException("minX mismatch at index $i: ${top.minX} != ${game.minX}")
            }
            if (top.maxX != game.maxX) {
                throw IllegalArgumentException("maxX mismatch at index $i: ${top.maxX} != ${game.maxX}")
            }
            if (top.minY != game.minY) {
                throw IllegalArgumentException("minY mismatch at index $i: ${top.minY} != ${game.minY}")
            }
            if (top.maxY != game.maxY) {
                throw IllegalArgumentException("maxY mismatch at index $i: ${top.maxY} != ${game.maxY}")
            }
        }
        // map tiles to their layers
        for (x in top.minX..top.maxX) {
            for (y in top.minY..top.maxY) {
                layers.forEachIndexed { layer, game ->
                    game[x, y]?.layer = layer
                }
            }
        }
    }

    override val tiles: List<Tile>
        get() = layers.flatMap(ShisenShoGame::tiles)

    override val remaining: Int
        get() = layers.sumOf(ShisenShoGame::remaining)

    override val total: Int
        get() = layers.sumOf(ShisenShoGame::total)

    override val seed: Int by lazy { System.identityHashCode(this) }

    override val minX: Int = layers[0].minX

    override val minY: Int = layers[0].minY

    override val maxX: Int = layers[0].maxX

    override val maxY: Int = layers[0].maxY

    override val gravity: Boolean = layers[0].gravity

    override val layersSize: Int = layers.size

    override fun move(a: Tile, b: Tile, movements: MutableList<Tile>?): Path {
        if (canMove(a, b).isEmpty()) {
            return emptyList()
        }
        val path = layers[a.layer].move(a, b, movements)
        if (path.isNotEmpty()) {
            cachedPossibleMoves = null
            history.add(a.layer)
        }
        return path
    }

    override fun canMove(a: Tile, b: Tile): Path {
        if (a.layer != b.layer) {
            return emptyList()
        }
        if (a.layer == 0) {
            return layers[0].canMove(a, b)
        }
        return possibleMoves()
            .firstOrNull { move -> a in move && b in move }
            ?.path
            ?: emptyList()
    }

    override fun hasMoves(): Boolean {
        val possibleMoves = cachedPossibleMoves ?: possibleMoves()
        return possibleMoves.isNotEmpty()
    }

    override fun canUndo(): Boolean {
        return history.isNotEmpty()
    }

    override fun undo() {
        if (history.isEmpty()) {
            return
        }
        cachedPossibleMoves = null
        val layer = history.removeLast()
        if (layer == HISTORY_SHUFFLE) {
            for (game in layers) {
                game.undo()
            }
        } else {
            layers[layer].undo()
        }
    }

    /**
     * Returns top-most tile at ([x]; [y]).
     *
     * Use `layer(index)[x, y]` to get a tile at `layer`.
     */
    override fun get(x: Int, y: Int): Tile? {
        for (game in layers) {
            val tile = game[x, y]
            if (tile != null) {
                return tile
            }
        }
        return null
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
        val possibleMoves = cachedPossibleMoves ?: possibleMoves()
        return possibleMoves.filter { move -> start in move }
    }

    override fun shuffle(hasMoves: Boolean, preserveLayout: Boolean, random: Random) {
        cachedPossibleMoves = null
        history.add(HISTORY_SHUFFLE)
        for (layer in layers) {
            // always preserve layout, otherwise it will break game rules
            layer.shuffle(hasMoves, true, random)
        }
    }

    override fun copy(): ShisenShoGame {
        return MultiLayerShisenShoGameImpl(*layers.map(ShisenShoGame::copy).toTypedArray())
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for ((index, layer) in layers.withIndex()) {
            sb.append("layer #$index:\n")
            sb.append(layer.toString())
        }
        return sb.toString()
    }

    override fun layer(index: Int): ShisenShoGame {
        return ReadOnly(layers[index], seed)
    }

    private fun findPossibleMoves(): List<Move> {
        val top = layers[0]
        val possibleMoves = ArrayList<Move>(top.possibleMoves())
        for (index in 1 until layers.size) {
            val game = layers[index]
            if (game.remaining == 0) {
                continue
            }
            val tiles = ArrayList<Tile>()
            for (x in game.minX..game.maxX) {
                for (y in game.minY..game.maxY) {
                    val tile = get(x, y)
                    if (tile?.layer == index) {
                        tiles.add(tile)
                    }
                }
            }
            possibleMoves.addAll(game.possibleMoves(tiles))
        }
        return possibleMoves
    }

    private fun ShisenShoGame.possibleMoves(tiles: MutableList<Tile>): List<Move> {
        if (tiles.size < 2) {
            return emptyList()
        }
        val moves = ArrayList<Move>()
        while (tiles.size > 1) {
            val a = tiles.removeFirst()
            for (b in tiles) {
                val path = canMove(a, b)
                if (path.isNotEmpty()) {
                    // we cannot remove tile at lower layer before we remove higher one,
                    // so situations like this are invalid:
                    //
                    // first layer:
                    //      A .
                    //      . A
                    // second layer:
                    //      . B
                    //      B .
                    //
                    // although here we can make B-B move on second layer,
                    // such cases cannot occur in the real game
                    //
                    // because of this, we always get valid paths here
                    moves += Move(a, b, path)
                }
            }
        }
        return moves
    }

    private class ReadOnly(
        private val game: ShisenShoGame,
        override val seed: Int
    ) : ShisenShoGame by game {

        override fun move(a: Tile, b: Tile, movements: MutableList<Tile>?): Path = emptyList()
        override fun hasMoves(): Boolean = false
        override fun canUndo(): Boolean = false
        override fun undo() = Unit
        override fun possibleMoves(): List<Move> = emptyList()
        override fun possibleMoves(start: Tile): List<Move> = emptyList()
        override fun shuffle(hasMoves: Boolean, preserveLayout: Boolean, random: Random) = Unit
        override fun toString(): String = game.toString()
    }
}
