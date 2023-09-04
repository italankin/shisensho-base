package com.italankin.shisensho.game

import kotlin.random.Random

interface ShisenShoGame {

    /**
     * Tiles on game field
     */
    val tiles: List<Tile>

    /**
     * Number of remaining tiles
     */
    val remaining: Int

    /**
     * Number of total tiles
     */
    val total: Int

    /**
     * Unique seed for this game.
     */
    val seed: Int

    /**
     * Minimum `x` coordinate value
     */
    val minX: Int

    /**
     * Minimum `y` coordinate value
     */
    val minY: Int

    /**
     * Maximum `x` coordinate value
     */
    val maxX: Int

    /**
     * Maximum `y` coordinate value
     */
    val maxY: Int

    /**
     * Whether this game has gravity enabled
     */
    val gravity: Boolean

    /**
     * Make a move with tiles [a] and [b].
     *
     * @param movements optional list where moved tiles will be written
     * @return a path from [a] to [b], empty if the move is not possible
     */
    fun move(a: Tile, b: Tile, movements: MutableList<Tile>? = null): Path

    /**
     * Check if path between [a] and [b] exists.
     *
     * @return a path from [a] to [b], empty if the move is not possible
     */
    fun canMove(a: Tile, b: Tile): Path

    /**
     * @return whether there are any moves
     */
    fun hasMoves(): Boolean

    /**
     * @return true, if the last move can be undone
     */
    fun canUndo(): Boolean

    /**
     * Undo last move
     */
    fun undo()

    /**
     * @return a tile at ([x]; [y]), or null if cell is empty
     */
    operator fun get(x: Int, y: Int): Tile?

    /**
     * A list of possible moves
     */
    fun possibleMoves(): List<Move>

    /**
     * A list of possible moves for with a given [start]
     */
    fun possibleMoves(start: Tile): List<Move>

    /**
     * Shuffle game field
     *
     * @param hasMoves - whether shuffled position should have at least one move
     * @param preserveLayout - keep empty cells positions
     * @param random - [Random] used to perform shuffling
     */
    fun shuffle(hasMoves: Boolean = true, preserveLayout: Boolean = true, random: Random = Random)

    /**
     * Create a copy of this game. The returned game may not reflect this game's undo state.
     *
     * @return a copy of this game
     */
    fun copy(): ShisenShoGame
}

/**
 * Width of the field (tiles + padding)
 */
val ShisenShoGame.fieldWidth
    get() = maxX + 1

/**
 * Height of the field (tiles + padding)
 */
val ShisenShoGame.fieldHeight
    get() = maxY + 1

/**
 * Width of the game (tiles only)
 */
val ShisenShoGame.gameWidth
    get() = maxX - 1

/**
 * Height of the game (tiles only)
 */
val ShisenShoGame.gameHeight
    get() = maxY - 1
