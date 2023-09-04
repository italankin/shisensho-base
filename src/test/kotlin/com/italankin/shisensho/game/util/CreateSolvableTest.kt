package com.italankin.shisensho.game.util

import com.italankin.shisensho.game.EqTileValueMatcher
import com.italankin.shisensho.game.ShisenShoGameGenerator
import com.italankin.shisensho.game.TileValue
import com.italankin.shisensho.game.impl.ShisenShoGameImpl
import com.italankin.shisensho.game.toTileValues
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.random.Random

@RunWith(Parameterized::class)
class CreateSolvableTest(
    private val gameFactory: ShisenShoGameGenerator.GameFactory
) {

    companion object {

        @Parameterized.Parameters(name = "game = {0}")
        @JvmStatic
        fun parameters(): List<ShisenShoGameGenerator.GameFactory> {
            return listOf(ShisenShoGameImpl.BfsFactory, ShisenShoGameImpl.SimpleFactory)
        }
    }

    @Test(timeout = 60_000)
    fun noGravity_smallGame() {
        val (width, tiles) = create(
            """
            A B C D
            D C B A
            E F E F
            """
        )
        val unsolvable = gameFactory.create(tiles, width, false, EqTileValueMatcher)
        assertFalse(unsolvable.isSolvable())
        val solvable = gameFactory.createSolvable(
            tiles,
            width,
            gravity = false,
            EqTileValueMatcher,
            Random(0)
        )
        assertTrue(solvable.isSolvable())
    }

    @Test
    fun noGravity() {
        val (width, tiles) = create(
            """
            V R J V L N Y X Q M 3 6 A K I 3 M 6
            N A O % M 1 A I @ W K K P B N Z T F
            B R 1 V D P 6 Q G U O S @ # W H G 2
            % Z 0 T L K J # 2 4 2 W U 4 N 4 A @
            # P J 1 E % 5 5 Y E O U D F Z 0 2 B
            P 0 Y G L Y E 3 V U Q D S X G Z B S
            R F I X O H R X T 1 0 Q S % M @ ? !
            W J I 6 3 D F 4 L H # 5 E H T 5 ! ?
            """
        )
        val solvable = gameFactory.createSolvable(
            tiles,
            width,
            gravity = false,
            EqTileValueMatcher,
            Random(0)
        )
        assertTrue(solvable.isSolvable())
    }

    @Test
    fun noGravity_pairs() {
        val (width, tiles) = create(
            """
            V R J V L N Y X Q M 3 6 A K I 3 M 6
            N A O % M 1 A I @ W K K P B N Z T F
            B R 1 V D P 6 Q G U O S @ # W H G 2
            % Z 0 T L K J # 2 4 2 W U 4 N 4 A @
            # P J 1 E % 5 5 Y E O U D F Z 0 2 B
            P 0 Y G L Y E 3 V U Q D S X G Z B S
            R F I X O H R X T 1 0 Q S % M @ ? !
            W J I 6 3 D F 4 L H # 5 E H T 5 ! ?
            """
        )
        val valueCounts = tiles.groupBy { it }.mapValues { it.value.size }
        val solvable = gameFactory.createSolvable(
            tiles,
            width,
            gravity = false,
            EqTileValueMatcher,
            Random(0)
        )
        val gameTiles = solvable.tiles
        for ((value, count) in valueCounts) {
            assertEquals(count, gameTiles.count { it.value == value })
        }
    }

    @Test(timeout = 60_000)
    fun gravity_smallGame() {
        val (width, tiles) = create(
            """
            A D C B D
            C B C B A
            D C A P Q
            B D A Q P
            """
        )
        val unsolvable = gameFactory.create(tiles, width, true, EqTileValueMatcher)
        assertFalse(unsolvable.isSolvable())
        val solvable = gameFactory.createSolvable(
            tiles,
            width,
            gravity = true,
            EqTileValueMatcher,
            Random(4)
        )
        assertTrue(solvable.isSolvable())
    }

    @Test(timeout = 60_000)
    @Ignore("Consumes huge amount of memory")
    fun gravity() {
        val (width, tiles) = create(
            """
            V R J V L N Y X Q M 3 6 A K I 3 M 6
            N A O % M 1 A I @ W K K P B N Z T F
            B R 1 V D P 6 Q G U O S @ # W H G 2
            % Z 0 T L K J # 2 4 2 W U 4 N 4 A @
            # P J 1 E % 5 5 Y E O U D F Z 0 2 B
            P 0 Y G L Y E 3 V U Q D S X G Z B S
            R F I X O H R X T 1 0 Q S % M @ ? !
            W J I 6 3 D F 4 L H # 5 E H T 5 ! ?
            """
        )
        val solvable = gameFactory.createSolvable(
            tiles,
            width,
            gravity = true,
            EqTileValueMatcher,
            Random(0)
        )
        assertTrue(solvable.isSolvable())
    }

    @Test
    fun gravity_pairs() {
        val (width, tiles) = create(
            """
            V R J V L N Y X Q M 3 6 A K I 3 M 6
            N A O % M 1 A I @ W K K P B N Z T F
            B R 1 V D P 6 Q G U O S @ # W H G 2
            % Z 0 T L K J # 2 4 2 W U 4 N 4 A @
            # P J 1 E % 5 5 Y E O U D F Z 0 2 B
            P 0 Y G L Y E 3 V U Q D S X G Z B S
            R F I X O H R X T 1 0 Q S % M @ ? !
            W J I 6 3 D F 4 L H # 5 E H T 5 ! ?
            """
        )
        val valueCounts = tiles.groupBy { it }.mapValues { it.value.size }
        val solvable = gameFactory.createSolvable(
            tiles,
            width,
            gravity = true,
            EqTileValueMatcher,
            Random(0)
        )
        val gameTiles = solvable.tiles
        for ((value, count) in valueCounts) {
            assertEquals(count, gameTiles.count { it.value == value })
        }
    }

    private fun create(s: String): Pair<Int, List<TileValue>> {
        val (width, tiles) = tilesFromString(s.trimIndent(), false)
        return width to tiles.toTileValues().map { it!! }
    }
}
