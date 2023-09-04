package com.italankin.shisensho.game.util

import com.italankin.shisensho.game.MultiLayerShisenShoGame
import com.italankin.shisensho.game.ShisenShoGame
import com.italankin.shisensho.game.gameHeight
import com.italankin.shisensho.game.gameWidth

/**
 * NB: Can take a long time and huge amount of memory.
 *
 * @return `true`, if game is solvable at least in one way
 * @throws [IllegalArgumentException] if game is invalid (e.g. cannot be solved by making valid
 * moves, regardless of tiles' positions)
 */
@Throws(IllegalArgumentException::class)
fun ShisenShoGame.isSolvable(): Boolean {
    if (remaining == 0) {
        return true
    }
    checkPairsMatching()
    if (this is MultiLayerShisenShoGame) {
        // checking layers independently are more efficient,
        // because we don't need to backtrack to previous layers if they are solvable
        return (0..<layersSize).all { layer(it).isSolvable() }
    }
    return copy().isSolvableRecursive(HashSet(gameWidth * gameHeight * 5))
}

/**
 * Fast way to check for invalid games (impossible to solve).
 *
 * @return `true` if game can be solved theoretically (all tiles have pairs)
 */
private fun ShisenShoGame.checkPairsMatching() {
    val tiles = ArrayList(tiles)
    while (tiles.isNotEmpty()) {
        val a = tiles.removeLast()
        val index = tiles.indexOfFirst { a.valueMatches(it) }
        if (index == -1) {
            throw IllegalArgumentException("Invalid game: has no matching tile for $a")
        }
        tiles.removeAt(index)
    }
}

private fun ShisenShoGame.isSolvableRecursive(visited: HashSet<String>): Boolean {
    if (remaining == 0) {
        return true
    }
    if (!visited.add(stateKey)) {
        return false
    }
    val possibleMoves = possibleMoves()
    for (move in possibleMoves) {
        move(move.a, move.b)
        if (isSolvableRecursive(visited)) {
            return true
        }
        undo()
    }
    return false
}

private val ShisenShoGame.stateKey: String
    get() {
        val s = StringBuilder(gameWidth * gameHeight)
        for (x in 1 until maxX) {
            for (y in 1 until maxY) {
                val tile = get(x, y)
                s.append(tile?.value?.c ?: '.')
            }
        }
        return s.toString()
    }
