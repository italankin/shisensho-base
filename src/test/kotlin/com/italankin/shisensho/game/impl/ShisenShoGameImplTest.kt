package com.italankin.shisensho.game.impl

import com.italankin.shisensho.game.ChineseStyleTileValueMatcher
import com.italankin.shisensho.game.EqTileValueMatcher
import com.italankin.shisensho.game.ShisenShoGameGenerator
import com.italankin.shisensho.game.Tile
import com.italankin.shisensho.game.TileValue
import com.italankin.shisensho.game.toTileValues
import com.italankin.shisensho.game.util.manhattan
import com.italankin.shisensho.game.util.simplify
import com.italankin.shisensho.game.util.tilesFromString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.random.Random

@RunWith(Parameterized::class)
class ShisenShoGameImplTest(
    private val gameFactory: ShisenShoGameGenerator.GameFactory
) {

    companion object {

        @Parameters(name = "game = {0}")
        @JvmStatic
        fun parameters(): List<ShisenShoGameGenerator.GameFactory> {
            return listOf(ShisenShoGameImpl.BfsFactory, ShisenShoGameImpl.SimpleFactory)
        }
    }

    @Test
    fun shuffle() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                A B
                B A
                """
            ),
            width = 2,
            gravity = false,
        )
        val beforeShuffle = game.tiles
        game.shuffle(hasMoves = true, random = Random(0))
        assertNotEquals(beforeShuffle, game.tiles)
    }

    @Test
    fun history() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                A A
                B B
                """
            ),
            width = 2,
            gravity = false,
        )
        assertFalse(game.canUndo())
        game.move(game[1, 1]!!, game[2, 1]!!)
        assertTrue(game.canUndo())
        assertNull(game[1, 1])
        assertNull(game[2, 1])
        game.undo()
        assertFalse(game.canUndo())
        assertEquals('A', game[1, 1]?.value?.c)
        assertEquals('A', game[2, 1]?.value?.c)
        assertEquals('B', game[1, 2]?.value?.c)
        assertEquals('B', game[2, 2]?.value?.c)
    }

    @Test
    fun historyShuffle() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                A B
                B A
                """
            ),
            width = 2,
            gravity = false,
        )
        val beforeShuffle = game.tiles
        game.shuffle(hasMoves = true, random = Random(0))
        assertNotEquals(beforeShuffle, game.tiles)
        assertTrue(game.canUndo())
    }

    @Test
    fun hasMoves() {
        val game1 = gameFactory.create(
            tiles = fromStr(
                """
                A B
                B A
                """
            ),
            width = 2,
            gravity = false,
        )
        assertFalse(game1.hasMoves())

        val game2 = gameFactory.create(
            tiles = fromStr(
                """
                A A
                B B
                """
            ),
            width = 2,
            gravity = false,
        )
        assertTrue(game2.hasMoves())

        val game3 = gameFactory.create(
            tiles = fromStr(
                """
                A B A .
                D C B .
                . C D .
                """
            ),
            width = 4,
            gravity = false,
        )
        assertTrue(game3.hasMoves())
    }

    @Test
    fun gravity_create() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                A B
                . A
                """
            ),
            width = 2,
            gravity = true,
        )
        assertEquals(null, game[1, 1])
        assertEquals('B', game[2, 1]?.value?.c)
        assertEquals('A', game[1, 2]?.value?.c)
        assertEquals('A', game[2, 2]?.value?.c)
    }

    @Test
    fun gravity_move() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                . E . .
                A C C .
                B B E A
                """
            ),
            width = 4,
            gravity = true,
        )
        game.move(game[1, 3]!!, game[2, 3]!!)
        assertEquals(null, game[1, 1])
        assertEquals(null, game[2, 1])
        assertEquals(null, game[1, 2])
        assertEquals('E', game[2, 2]?.value?.c)
        assertEquals('A', game[1, 3]?.value?.c)
        assertEquals('C', game[2, 3]?.value?.c)
    }

    @Test
    fun gravity_movements() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                . E . .
                A C C .
                B B E A
                """
            ),
            width = 4,
            gravity = true,
        )
        val movements = ArrayList<Tile>()
        game.move(game[1, 3]!!, game[2, 3]!!, movements)
        assertEquals(3, movements.size)
        assertTrue(movements.any { it.value.c == 'A' })
        assertTrue(movements.any { it.value.c == 'C' })
        assertTrue(movements.any { it.value.c == 'E' })
    }

    @Test
    fun total_remaining() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                A A
                B B
                """
            ),
            width = 2,
            gravity = false,
        )
        assertEquals(4, game.total)
        assertEquals(4, game.remaining)

        game.move(game[1, 1]!!, game[2, 1]!!)
        assertEquals(4, game.total)
        assertEquals(2, game.remaining)
    }

    @Test
    fun possibleMoves_noMoves() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                A B
                B A
                """
            ),
            width = 2,
            gravity = false,
        )
        val possibleMoves = game.possibleMoves()
        assertTrue(possibleMoves.isEmpty())
    }

    @Test
    fun possibleMoves_paths() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                A . . A
                B C D E
                G F G B
                . . . F
                """
            ),
            width = 4,
            gravity = false,
        )
        val possibleMoves = game.possibleMoves()
        for ((index, move) in possibleMoves.withIndex()) {
            for (i in 1 until move.path.lastIndex) {
                val (x, y) = move.path[i]
                assertNull("moveIndex=$index, pointIndex=$i", game[x, y])
            }
        }
        for ((moveIndex, move) in possibleMoves.withIndex()) {
            for (i in 1 until move.path.lastIndex) {
                val (x0, y0) = move.path[i - 1]
                val (x1, y1) = move.path[i]
                assertEquals("moveIndex=$moveIndex, pointIndex=$i", 1, manhattan(x0, y0, x1, y1))
            }
        }
    }

    @Test
    fun possibleMoves() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                V N 5 K B Q F R 2 E W X J 6 Q T 2 J
                Q B Y X % N S O O F O G R   % L T M
                V T B N K Y L H R O # 6 R F I   X %
                Y   Q 4 E 1 D P M X E Z A B H E I L
                M S C 2 A N U P # A W W D 1 W J C 0
                S 2 C I J Z 0 S U 5 D 3 4 V V 4 Z Z
                1 T L K 0 H P P U U 3 I C 3 5 %   Y
                4 G 6 A # 1 H M 3 K # G D G 5 0 6 F 
                """
            ),
            width = 18,
            gravity = false
        )
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
        val (width, tiles) = tilesFromString(
            """
            V N 5 K B Q F R 2 E W X J 6 Q T 2 J
            Q B Y X % S N O O F O G R   % L T M
            V T B N K Y L H R O # 6 R F I   X %
            Y   Q 4 E 1 D P M X E Z A B H E I L
            M S C 2 A N U P # A W W D 1 W J C 0
            S 2 C I J Z 0 S U 5 D 3 4 V V 4 Z Z
            1 T L K 0 H P P U U 3 I C 3 5 %   Y
            4 G 6 A # 1 H M 3 K # G D G 5 0 6 F 
            """.trimIndent(),
            padded = false
        )
        val game = gameFactory.create(
            tiles = tiles.toTileValues(),
            width = width,
            gravity = false
        )
        assertEquals(20, game.possibleMoves().size)
        game.move(game[6, 1]!!, game[15, 1]!!)
        assertEquals(19, game.possibleMoves().size)
        game.undo()
        assertEquals(20, game.possibleMoves().size)
    }

    @Test
    fun possibleMoves_count_shuffle() {
        val (width, tiles) = tilesFromString(
            """
            V N 5 K B Q F R 2 E W X J 6 Q T 2 J
            Q B Y X % S N O O F O G R   % L T M
            V T B N K Y L H R O # 6 R F I   X %
            Y   Q 4 E 1 D P M X E Z A B H E I L
            M S C 2 A N U P # A W W D 1 W J C 0
            S 2 C I J Z 0 S U 5 D 3 4 V V 4 Z Z
            1 T L K 0 H P P U U 3 I C 3 5 %   Y
            4 G 6 A # 1 H M 3 K # G D G 5 0 6 F 
            """.trimIndent(),
            padded = false
        )
        val game = gameFactory.create(
            tiles = tiles.toTileValues(),
            width = width,
            gravity = false
        )
        assertEquals(20, game.possibleMoves().size)
        game.shuffle(random = Random(0))
        assertEquals(12, game.possibleMoves().size)
        game.undo()
        assertEquals(20, game.possibleMoves().size)
    }

    @Test
    fun possibleMoves_oneSegment_nearby() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                A A
                B B
                """
            ),
            width = 2,
            gravity = false,
        )
        val possibleMoves = game.possibleMoves()
        assertEquals(2, possibleMoves.size)
        assertTrue(possibleMoves.any { move ->
            game[1, 1]!! in move && game[2, 1]!! in move
        })
        assertTrue(possibleMoves.any { move ->
            game[1, 2]!! in move && game[2, 2]!! in move
        })
        assertTrue(possibleMoves.all { it.path.size == 2 })
    }

    @Test
    fun possibleMoves_oneSegment() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                J C D E F
                K B A . A
                L . G H I
                M B . . .
                """
            ),
            width = 5,
            gravity = false,
        )
        val possibleMoves = game.possibleMoves()
        assertEquals(2, possibleMoves.size)
        assertTrue(possibleMoves.any { move ->
            game[3, 2]!! in move && game[5, 2]!! in move
        })
        assertTrue(possibleMoves.any { move ->
            game[2, 2]!! in move && game[2, 4]!! in move
        })
        assertTrue(possibleMoves.all { move ->
            move.path.simplify().size == 2
        })
    }

    @Test
    fun possibleMoves_twoSegments() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                J C D E F
                K B . . A
                L . G H I
                M . . . B
                N O P Q R
                """
            ),
            width = 5,
            gravity = false,
        )
        val possibleMoves = game.possibleMoves()
        assertEquals(1, possibleMoves.size)
        val move = possibleMoves.first()
        assertTrue(game[2, 2]!! in move)
        assertTrue(game[5, 4]!! in move)
        assertEquals(3, move.path.simplify().size)
    }

    @Test
    fun possibleMoves_threeSegments() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                J C D E F
                K . . B A
                L . G H I
                M . . . B
                N O P Q R
                """
            ),
            width = 5,
            gravity = false,
        )
        val possibleMoves = game.possibleMoves()
        assertEquals(1, possibleMoves.size)
        val move = possibleMoves.first()
        assertTrue(game[4, 2]!! in move)
        assertTrue(game[5, 4]!! in move)
        assertEquals(4, move.path.simplify().size)
    }

    @Test
    fun possibleMoves_startTile() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                . C . C .
                . B . A .
                . . A . .
                D B . . D
                """
            ),
            width = 5,
            gravity = false,
        )
        val possibleMoves = game.possibleMoves(game[4, 2]!!)
        assertFalse(possibleMoves.isEmpty())
        val move = possibleMoves.first()
        assertTrue(game[4, 2]!! in move)
        assertTrue(game[3, 3]!! in move)
    }

    @Test
    fun possibleMoves_startTile_noMoves() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                . C . C .
                A B . A .
                . . D . .
                D B . . .
                """
            ),
            width = 5,
            gravity = false,
        )
        val possibleMoves = game.possibleMoves(game[4, 2]!!)
        assertTrue(possibleMoves.isEmpty())
    }

    @Test
    fun fullSolve() {
        val game = gameFactory.create(
            tiles = fromStr(
                """
                D G I J J
                C F G H I
                B E F E H
                A B A C D
                """
            ),
            width = 5,
            gravity = false,
        )
        do {
            val possibleMoves = game.possibleMoves()
            if (possibleMoves.isEmpty()) {
                break
            }
            val move = possibleMoves.first()
            game.move(move.a, move.b)
        } while (true)
        assertTrue(game.remaining == 0)
    }

    @Test
    fun chineseStyle_flowers() {
        val game1 = gameFactory.create(
            tiles = TileValue.FLOWERS,
            width = 2,
            gravity = false,
            tileValueMatcher = ChineseStyleTileValueMatcher
        )
        assertEquals(4, game1.possibleMoves().size)

        val game2 = gameFactory.create(
            tiles = TileValue.FLOWERS,
            width = 2,
            gravity = false,
            tileValueMatcher = EqTileValueMatcher
        )
        assertTrue(game2.possibleMoves().isEmpty())
    }

    @Test
    fun chineseStyle_seasons() {
        val game1 = gameFactory.create(
            tiles = TileValue.SEASONS,
            width = 2,
            gravity = false,
            tileValueMatcher = ChineseStyleTileValueMatcher
        )
        assertEquals(4, game1.possibleMoves().size)

        val game2 = gameFactory.create(
            tiles = TileValue.SEASONS,
            width = 2,
            gravity = false,
            tileValueMatcher = EqTileValueMatcher
        )
        assertTrue(game2.possibleMoves().isEmpty())
    }

    private fun fromStr(s: String, padded: Boolean = false): List<TileValue?> {
        val (_, tiles) = tilesFromString(s.trimIndent(), padded)
        return tiles.toTileValues()
    }
}
