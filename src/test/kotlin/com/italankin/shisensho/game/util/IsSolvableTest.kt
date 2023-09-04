package com.italankin.shisensho.game.util

import com.italankin.shisensho.game.EqTileValueMatcher
import com.italankin.shisensho.game.MultiLayerShisenShoGameImpl
import com.italankin.shisensho.game.ShisenShoGame
import com.italankin.shisensho.game.impl.ShisenShoGameImpl
import com.italankin.shisensho.game.toTileValues
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IsSolvableTest {

    @Test(timeout = 60_000)
    fun notSolvable() {
        val (width, tiles) = tilesFromString(
            """
            . . . . . . . . . .
            . H F G H K J K I .
            . L D P Q D H J C .
            . F B Q P K B H G .
            . E C J D G E A F .
            . E I I K F C L I .
            . A L L E C D J G .
            . . . . . . . . . .
            """.trimIndent(),
            true
        )
        val game = create(tiles, width, gravity = false)
        assertFalse(game.isSolvable())
    }

    @Test(timeout = 60_000)
    fun solvable() {
        val (width, tiles) = tilesFromString(
            """
            . . . . . . . . . .
            . H F G H K J K I .
            . L D A A D H J C .
            . F B B B K B H G .
            . E C J D G E A F .
            . E I I K F C L I .
            . A L L E C D J G .
            . . . . . . . . . .
            """.trimIndent(),
            true
        )
        val game = create(tiles, width, gravity = false)
        assertTrue(game.isSolvable())
    }

    @Test(timeout = 60_000)
    fun solvable_gravity() {
        val (width, tiles) = tilesFromString(
            """
            . . . . . . . . . .
            . H F G H K J K I .
            . L D A A D H J C .
            . F B B B K B H G .
            . E C J D G E A F .
            . E I I K F C L I .
            . A L L E C D J G .
            . . . . . . . . . .
            """.trimIndent(),
            true
        )
        val game = create(tiles, width, gravity = true)
        assertTrue(game.isSolvable())
    }

    @Test(timeout = 60_000)
    fun solvable_multiLayer() {
        val (width, tiles1) = tilesFromString(
            """
            . . . . . . . . . .
            . I A F J L I D J .
            . F F G D A B H F .
            . I C D K E A C H .
            . I K J H J H L B .
            . L C L E B B E C .
            . K K G D G G E A .
            . . . . . . . . . .
            """.trimIndent(),
            true
        )
        val (_, tiles2) = tilesFromString(
            """
            . . . . . . . . . .
            . I C G A B D E H .
            . K J D L F A J I .
            . D L C J J H I C .
            . I A G E K A L E .
            . B G E H K F K H .
            . G B F D F C L B .
            . . . . . . . . . .
            """.trimIndent(),
            true
        )
        val game1 = create(tiles1, width, gravity = false)
        val game2 = create(tiles2, width, gravity = false)
        val game = MultiLayerShisenShoGameImpl(game1, game2)
        assertTrue(game.isSolvable())
    }

    @Test(timeout = 60_000)
    fun notSolvable_multiLayer() {
        val (width, tiles1) = tilesFromString(
            """
            . . . . . . . . . .
            . I A F J L I D J .
            . F F G D A B H F .
            . I C D K E A C H .
            . I K J H J H L B .
            . L C L E ? ! E C .
            . K K G D ! ? E A .
            . . . . . . . . . .
            """.trimIndent(),
            true
        )
        val (_, tiles2) = tilesFromString(
            """
            . . . . . . . . . .
            . I C G A B D E H .
            . K J D L F A J I .
            . D L C J J H I C .
            . I A G E K A L E .
            . B G E H K F K H .
            . G B F D F C L B .
            . . . . . . . . . .
            """.trimIndent(),
            true
        )
        val game1 = create(tiles1, width, gravity = false)
        val game2 = create(tiles2, width, gravity = false)
        val game = MultiLayerShisenShoGameImpl(game1, game2)
        assertFalse(game.isSolvable())
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidGame() {
        val (width, tiles) = tilesFromString(
            """
            B L X B E X I O A D 2 # 6 T # % 0 G
            Z 2 D   R F D J C U P S 6 E % O 6 G
            # S % V Z Q V I A 5 T L   1 N 3 Q X
            I L C % M K A 0 Q C H Z P N R Y U 1
            3 # Y X C P I J V P W B B O M Y J H
            L R K Z A 0 5 N E F F 4 H 3   Y T 5
            S 0 F W M S O 2 5 W 4 V 4 E U   6 H
            U J 3 K N 2 T G 1 1 M W Q G K R 4 ?
            """.trimIndent(),
            false
        )
        val game = create(tiles, width, gravity = false)
        game.isSolvable()
    }

    private fun create(tiles: List<Char?>, width: Int, gravity: Boolean): ShisenShoGame {
        return ShisenShoGameImpl.BfsFactory.create(
            tiles.toTileValues(),
            width,
            gravity,
            EqTileValueMatcher
        )
    }
}
