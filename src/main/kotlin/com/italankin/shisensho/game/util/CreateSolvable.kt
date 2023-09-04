package com.italankin.shisensho.game.util

import com.italankin.shisensho.game.Point
import com.italankin.shisensho.game.ShisenShoGame
import com.italankin.shisensho.game.ShisenShoGameGenerator
import com.italankin.shisensho.game.Tile
import com.italankin.shisensho.game.TileId
import com.italankin.shisensho.game.TileValue
import com.italankin.shisensho.game.TileValueMatcher
import kotlin.random.Random

/**
 * Create solvable game from given [state].
 */
fun ShisenShoGameGenerator.GameFactory.createSolvable(
    state: List<TileValue?>,
    width: Int,
    gravity: Boolean,
    tileValueMatcher: TileValueMatcher,
    random: Random = Random
): ShisenShoGame {
    // The game is solvable, when one of it's sub-states are solvable, recursively.
    // Idea behind algorithm is to solve the game by making random moves
    // and update `state` when we're successfully make a move.
    // If we run out of moves and game is not solved, we shuffle and continue until no tiles left.
    if (gravity) {
        return createSolvableWithGravity(state, width, tileValueMatcher, random)
    }
    val result = ArrayList(state)
    val game = create(state, width, false, tileValueMatcher)
    if (game.remaining == 0) {
        throw IllegalArgumentException("game is empty")
    }
    fun Tile.index(): Int {
        return (y - 1) * width + (x - 1)
    }
    do {
        var possibleMoves = game.possibleMoves()
        if (possibleMoves.isEmpty()) {
            game.shuffle(hasMoves = true, preserveLayout = true, random = random)
            possibleMoves = game.possibleMoves()
        }
        val move = possibleMoves.random(random)
        game.move(move.a, move.b)
        // update derived state with new tile position (after possible shuffle),
        // like it was there from the beginning
        result[move.a.index()] = move.a.value
        result[move.b.index()] = move.b.value
    } while (game.remaining > 0)
    return create(result, width, false, tileValueMatcher)
}

private fun ShisenShoGameGenerator.GameFactory.createSolvableWithGravity(
    state: List<TileValue?>,
    width: Int,
    tileValueMatcher: TileValueMatcher,
    random: Random = Random
): ShisenShoGame {
    // Algorithm for games with gravity is almost the same.
    // We just need to get tile's position from start state, not current position.
    val result = ArrayList(state)
    val game = create(state, width, true, tileValueMatcher)
    if (game.remaining == 0) {
        throw IllegalArgumentException("game is empty")
    }
    fun Tile.index(): Int {
        return (y - 1) * width + (x - 1)
    }
    // create a snapshot of start state
    val startState = game.tiles.associate { tile -> tile.id to tile.copy() }
    do {
        var possibleMoves = game.possibleMoves()
        if (possibleMoves.isEmpty()) {
            val beforeShuffle = game.tiles
            game.shuffle(hasMoves = true, preserveLayout = true, random = random)
            val afterShuffle = game.tiles
            val movements = HashMap<TileId, Point>()
            for (i in afterShuffle.indices) {
                val a = startState[afterShuffle[i].id]!!
                val b = startState[beforeShuffle[i].id]!!
                // collect previous positions of shuffled tiles
                movements[a.id] = Point(b.x, b.y)
            }
            for ((id, p) in movements) {
                // update start state positions
                startState[id]!!.set(p.x, p.y)
            }
            possibleMoves = game.possibleMoves()
        }
        val move = possibleMoves.random(random)
        game.move(move.a, move.b)
        // update derived state
        val a = startState[move.a.id]!!
        result[a.index()] = a.value
        val b = startState[move.b.id]!!
        result[b.index()] = b.value
    } while (game.remaining > 0)
    return create(result, width, true, tileValueMatcher)
}
