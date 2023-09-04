package com.italankin.shisensho.game

import com.italankin.shisensho.game.impl.ShisenShoGameImpl

fun main() {
    val generator = ShisenShoGameGenerator(gameFactory = ShisenShoGameImpl.BfsFactory)
    val game = generator.generate(6, 4, 2)
    println("Game:")
    println(game)
    while (game.remaining > 0 || game.hasMoves()) {
        val moves = game.possibleMoves()
        val move = moves.first()
        println("${move.a} <-> ${move.b}:")
        game.move(move.a, move.b)
        println(game)
    }
    if (game.remaining == 0) {
        println("Solved")
    } else {
        println("No moves")
    }
}