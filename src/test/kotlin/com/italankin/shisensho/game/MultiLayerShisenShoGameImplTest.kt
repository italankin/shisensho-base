package com.italankin.shisensho.game

import com.italankin.shisensho.game.impl.ShisenShoGameImpl
import com.italankin.shisensho.game.util.tilesFromString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class MultiLayerShisenShoGameImplTest {

    @Test
    fun history() {
        val l1 = create(
            """
            A A
            B B
            """
        )
        val l2 = create(
            """
            A A
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        game.move(game[1, 1]!!, game[2, 1]!!)
        assertNull(l1[1, 1])
        assertNull(l1[2, 1])
        assertTrue(game.canUndo())
        game.undo()
        assertEquals('A', l1[1, 1]?.value?.c)
        assertEquals('A', l1[2, 1]?.value?.c)
    }

    @Test
    fun shuffle() {
        val l1 = create(
            """
            . .
            B B
            """
        )
        val l2 = create(
            """
            A B
            A B
            """
        )
        val l1prev = l1.tiles
        val l2prev = l2.tiles
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        game.shuffle(random = Random(0))
        assertFalse(l1.tiles == l1prev && l2.tiles == l2prev)
    }

    @Test
    fun historyShuffle() {
        val l1 = create(
            """
            . .
            B B
            """
        )
        val l2 = create(
            """
            A B
            A B
            """
        )
        val l1prev = l1.tiles
        val l2prev = l2.tiles
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        game.shuffle(random = Random(0))
        assertTrue(game.canUndo())
        game.undo()
        assertTrue(l1.tiles == l1prev && l2.tiles == l2prev)
    }

    @Test
    fun possibleMoves_fromMultipleLayers() {
        val l1 = create(
            """
            . .
            B B
            """
        )
        val l2 = create(
            """
            A A
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        val possibleMoves = game.possibleMoves()
        assertEquals(2, possibleMoves.size)
        assertTrue(possibleMoves.any { move ->
            move.contains(l1[1, 2]!!) && move.contains(l1[2, 2]!!)
        })
        assertTrue(possibleMoves.any { move ->
            move.contains(l2[1, 1]!!) && move.contains(l2[2, 1]!!)
        })
    }

    @Test
    fun possibleMoves_noMoves() {
        val l1 = create(
            """
            A B
            B A
            """
        )
        val l2 = create(
            """
            A A
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        val possibleMoves = game.possibleMoves()
        assertTrue(possibleMoves.isEmpty())
    }

    @Test
    fun possibleMoves_tileStart() {
        val l1 = create(
            """
            . .
            B B
            """
        )
        val l2 = create(
            """
            A A
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        val possibleMoves = game.possibleMoves(l1[1, 2]!!)
        assertEquals(1, possibleMoves.size)
        val move = possibleMoves.first()
        assertTrue(l1[1, 2]!! in move)
        assertTrue(l1[2, 2]!! in move)
    }

    @Test
    fun possibleMoves_tileStart_noMoves() {
        val l1 = create(
            """
            . .
            B B
            """
        )
        val l2 = create(
            """
            A A
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        val possibleMoves = game.possibleMoves(l2[1, 2]!!)
        assertTrue(possibleMoves.isEmpty())
    }

    @Test
    fun possibleMoves() {
        val l1 = create(
            """
            . .
            B B
            """
        )
        val l2 = create(
            """
            A A
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        val possibleMoves = game.possibleMoves()
        assertTrue(possibleMoves.isNotEmpty())
        for (move in possibleMoves) {
            val path = game.move(move.a, move.b)
            assertTrue(path.isNotEmpty())
            game.undo()
        }
    }

    @Test
    fun possibleMoves_count_move() {
        val l1 = create(
            """
            . .
            B B
            """
        )
        val l2 = create(
            """
            A A
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        assertEquals(2, game.possibleMoves().size)
        game.move(game[1, 1]!!, game[2, 1]!!)
        assertEquals(1, game.possibleMoves().size)
        game.undo()
        assertEquals(2, game.possibleMoves().size)
    }

    @Test
    fun possibleMoves_count_shuffle() {
        val l1 = create(
            """
            A B
            B A
            """
        )
        val l2 = create(
            """
            A A
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        assertEquals(0, game.possibleMoves().size)
        game.shuffle(random = Random(0))
        assertEquals(2, game.possibleMoves().size)
        game.undo()
        assertEquals(0, game.possibleMoves().size)
    }

    @Test
    fun get() {
        val l1 = create(
            """
            . B
            B .
            """
        )
        val l2 = create(
            """
            A A
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        assertEquals(game[1, 1]!!, l2[1, 1]!!)
        assertEquals(game[2, 1]!!, l1[2, 1]!!)
        assertEquals(game[1, 2]!!, l1[1, 2]!!)
        assertEquals(game[2, 2]!!, l2[2, 2]!!)
    }

    @Test
    fun move_topLayer() {
        val l1 = create(
            """
            . .
            B B
            """
        )
        val l2 = create(
            """
            A B
            B A
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        game.move(game[1, 2]!!, game[2, 2]!!)
        assertEquals(0, l1.remaining)
        assertEquals(4, l2.remaining)
        assertEquals(4, game.remaining)
    }

    @Test
    fun move_bottomLayer() {
        val l1 = create(
            """
            . .
            B B
            """
        )
        val l2 = create(
            """
            A A
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        game.move(game[1, 1]!!, game[2, 1]!!)
        assertEquals(2, l1.remaining)
        assertEquals(2, l2.remaining)
        assertEquals(4, game.remaining)
    }

    @Test
    fun move_bottomLayer_obstructed() {
        val l1 = create(
            """
            . .
            B B
            """
        )
        val l2 = create(
            """
            A C
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        val path = game.move(l2[1, 2]!!, l2[2, 2]!!)
        assertTrue(path.isEmpty())
        assertEquals(2, l1.remaining)
        assertEquals(4, l2.remaining)
        assertEquals(6, game.remaining)
    }

    @Test
    fun move_bottomLayer_obstructed2() {
        val l1 = create(
            """
            A B
            B A
            """
        )
        val l2 = create(
            """
            A A
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        val possibleMoves = game.possibleMoves()
        assertTrue(possibleMoves.isEmpty())
    }

    @Test
    fun move_differentLayers() {
        val l1 = create(
            """
            A A
            B B
            """
        )
        val l2 = create(
            """
            A A
            B B
            """
        )
        val game = MultiLayerShisenShoGameImpl(l1, l2)
        val path = game.move(l1[1, 1]!!, l2[2, 1]!!)
        assertTrue(path.isEmpty())
    }

    private fun create(s: String): ShisenShoGame {
        val (_, tiles) = tilesFromString(s.trimIndent(), false)
        return ShisenShoGameImpl.BfsFactory.create(tiles.toTileValues(), 2, false)
    }
}
