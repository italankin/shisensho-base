package com.italankin.shisensho.game

import com.italankin.shisensho.game.ShisenShoGameGenerator.GameFactory
import com.italankin.shisensho.game.util.createSolvable
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * Generator for [ShisenShoGame]s.
 *
 * @param gameFactory [GameFactory] which is used to create actual implementations
 * @param random optional [Random] instance
 * @param hasMoves whether created games should have at least one legal move
 */
class ShisenShoGameGenerator(
    private val gameFactory: GameFactory,
    private val random: Random = Random,
    private val hasMoves: Boolean = true
) {

    private val alphabets = ConcurrentHashMap<Int, List<TileValue>>(1)

    fun generate(
        width: Int,
        height: Int,
        pairsCount: Int,
        gravity: Boolean = false,
        chineseStyle: Boolean = false,
        solvable: Boolean = false
    ): ShisenShoGame {
        if ((width * height) % (pairsCount * 2) != 0) {
            throw IllegalArgumentException("`width * height` must be divisible by `pairsCount * 2`!")
        }
        val valueMatcher = TileValueMatcher(chineseStyle)
        val key = pairsCount + if (chineseStyle) 100000 else 0
        val tiles = alphabets
            .getOrPut(key) { TileValue.createAlphabet(pairsCount, chineseStyle) }
            .subList(0, width * height)
            .toMutableList()
        do {
            tiles.shuffle(random)
            if (solvable) {
                return gameFactory.createSolvable(tiles, width, gravity, valueMatcher, random)
            }
            val game = gameFactory.create(tiles, width, gravity, valueMatcher)
            if (!hasMoves || game.hasMoves()) {
                return game
            }
        } while (true)
    }

    fun generate(
        width: Int,
        height: Int,
        pairsCount: Int,
        layers: Int,
        gravity: Boolean = false,
        chineseStyle: Boolean = false,
        solvable: Boolean = false,
    ): ShisenShoGame {
        if (layers < 1) {
            throw IllegalArgumentException("layers must be >= 1")
        } else if (layers == 1) {
            return generate(width, height, pairsCount, gravity, chineseStyle, solvable)
        }
        val games = (0..<layers)
            .map { generate(width, height, pairsCount, gravity, chineseStyle, solvable) }
            .toTypedArray()
        return MultiLayerShisenShoGameImpl(*games)
    }

    interface GameFactory {

        fun create(
            tiles: List<TileValue?>,
            width: Int,
            gravity: Boolean,
            tileValueMatcher: TileValueMatcher = EqTileValueMatcher
        ): ShisenShoGame
    }
}
