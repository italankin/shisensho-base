package com.italankin.shisensho.game.util

import com.italankin.shisensho.game.Point
import org.junit.Assert.assertEquals
import org.junit.Test

class SimplifyPathTest {

    @Test
    fun emptyPath() {
        val path = listOf<Point>()
        assertEquals(emptyList<Point>(), path.simplify())
    }

    @Test
    fun singlePoint() {
        val path = listOf(
            Point(1, 1),
        )
        val expected = listOf(
            Point(1, 1),
        )
        assertEquals(expected, path.simplify())
    }

    @Test
    fun oneSegment_4() {
        /////////////////
        //   1   2   3 //
        // 1 ║         //
        // 2 ║         //
        // 4 ║         //
        // 4 ║         //
        /////////////////
        val path = listOf(
            Point(1, 1),
            Point(1, 2),
            Point(1, 3),
            Point(1, 4),
        )
        val expected = listOf(
            Point(1, 1),
            Point(1, 4),
        )
        assertEquals(expected, path.simplify())
    }

    @Test
    fun oneSegment_2() {
        /////////////////
        //   1   2   3 //
        // 1 ═════     //
        // 2           //
        // 3           //
        /////////////////
        val path = listOf(
            Point(1, 1),
            Point(2, 1),
        )
        val expected = listOf(
            Point(1, 1),
            Point(2, 1),
        )
        assertEquals(expected, path.simplify())
    }

    @Test
    fun oneSegment_simplified() {
        /////////////////
        //   1   2   3 //
        // 1 ║         //
        // 2 ║         //
        // 4 ║         //
        // 4 ║         //
        /////////////////
        val path = listOf(
            Point(1, 1),
            Point(1, 4),
        )
        val expected = listOf(
            Point(1, 1),
            Point(1, 4),
        )
        assertEquals(expected, path.simplify())
    }

    @Test(expected = IllegalStateException::class)
    fun oneSegment_impossible() {
        val path = listOf(
            Point(1, 1),
            Point(3, 2),
        )
        path.simplify()
    }

    @Test
    fun twoSegments_4_4() {
        /////////////////////
        //   1   2   3   4 //
        // 1 ║             //
        // 2 ║             //
        // 3 ║             //
        // 4 ╚════════════ //
        /////////////////////
        val path = listOf(
            Point(1, 1),
            Point(1, 2),
            Point(1, 3),
            Point(1, 4),
            Point(2, 4),
            Point(3, 4),
            Point(4, 4),
        )
        val expected = listOf(
            Point(1, 1),
            Point(1, 4),
            Point(4, 4),
        )
        assertEquals(expected, path.simplify())
    }

    @Test
    fun twoSegments_2_2() {
        /////////////////
        //   1   2   3 //
        // 1 ║         //
        // 2 ╚════     //
        // 3           //
        /////////////////
        val path = listOf(
            Point(1, 1),
            Point(1, 2),
            Point(2, 2),
        )
        val expected = listOf(
            Point(1, 1),
            Point(1, 2),
            Point(2, 2),
        )
        assertEquals(expected, path.simplify())
    }

    @Test
    fun twoSegments_simplified() {
        /////////////////////
        //   1   2   3   4 //
        // 1 ║             //
        // 2 ║             //
        // 3 ║             //
        // 4 ╚════════════ //
        /////////////////////
        val path = listOf(
            Point(1, 1),
            Point(1, 4),
            Point(4, 4),
        )
        val expected = listOf(
            Point(1, 1),
            Point(1, 4),
            Point(4, 4),
        )
        assertEquals(expected, path.simplify())
    }

    @Test(expected = IllegalStateException::class)
    fun twoSegments_impossible() {
        val path = listOf(
            Point(1, 1),
            Point(2, 1),
            Point(3, 2),
        )
        path.simplify()
    }

    @Test
    fun threeSegments_2_2_2() {
        /////////////////
        //   1   2   3 //
        // 1 ║   ║     //
        // 2 ╚═══╝     //
        // 3           //
        /////////////////
        val path = listOf(
            Point(1, 1),
            Point(1, 2),
            Point(2, 2),
            Point(2, 1),
        )
        val expected = listOf(
            Point(1, 1),
            Point(1, 2),
            Point(2, 2),
            Point(2, 1),
        )
        assertEquals(expected, path.simplify())
    }

    @Test
    fun threeSegments_3_2_3() {
        /////////////////
        //   1   2   3 //
        // 1 ║   ║     //
        // 2 ║   ║     //
        // 3 ╚═══╝     //
        /////////////////
        val path = listOf(
            Point(1, 1),
            Point(1, 2),
            Point(1, 3),
            Point(2, 3),
            Point(2, 2),
            Point(2, 1),
        )
        val expected = listOf(
            Point(1, 1),
            Point(1, 3),
            Point(2, 3),
            Point(2, 1),
        )
        assertEquals(expected, path.simplify())
    }

    @Test
    fun threeSegments_4_4_4() {
        /////////////////////
        //   1   2   3   4 //
        // 1 ════════════╗ //
        // 2             ║ //
        // 3             ║ //
        // 4 ════════════╝ //
        /////////////////////
        val path = listOf(
            Point(1, 1),
            Point(2, 1),
            Point(3, 1),
            Point(4, 1),
            Point(4, 2),
            Point(4, 3),
            Point(4, 4),
            Point(3, 4),
            Point(2, 4),
            Point(1, 4),
        )
        val expected = listOf(
            Point(1, 1),
            Point(4, 1),
            Point(4, 4),
            Point(1, 4),
        )
        assertEquals(expected, path.simplify())
    }

    @Test
    fun threeSegments_3_2_2() {
        /////////////////////
        //   1   2   3   4 //
        // 1 ════════╗     //
        // 2         ╚════ //
        // 3               //
        /////////////////////
        val path = listOf(
            Point(1, 1),
            Point(2, 1),
            Point(3, 1),
            Point(3, 2),
            Point(4, 2),
        )
        val expected = listOf(
            Point(1, 1),
            Point(3, 1),
            Point(3, 2),
            Point(4, 2),
        )
        assertEquals(expected, path.simplify())
    }

    @Test
    fun threeSegments_4_3_2() {
        /////////////////////
        //   1   2   3   4 //
        // 1 ════════════╗ //
        // 2             ║ //
        // 3     ════════╝ //
        /////////////////////
        val path = listOf(
            Point(1, 1),
            Point(2, 1),
            Point(3, 1),
            Point(4, 1),
            Point(4, 2),
            Point(4, 3),
            Point(3, 3),
            Point(2, 3),
        )
        val expected = listOf(
            Point(1, 1),
            Point(4, 1),
            Point(4, 3),
            Point(2, 3),
        )
        assertEquals(expected, path.simplify())
    }

    @Test
    fun threeSegments_duplicates() {
        /////////////////////
        //   1   2   3   4 //
        // 1 ════════════╗ //
        // 2             ║ //
        // 3     ════════╝ //
        /////////////////////
        val path = listOf(
            Point(1, 1),
            Point(2, 1),
            Point(3, 1),
            Point(3, 1),
            Point(3, 1),
            Point(4, 1),
            Point(4, 2),
            Point(4, 3),
            Point(4, 3),
            Point(4, 3),
            Point(3, 3),
            Point(3, 3),
            Point(2, 3),
        )
        val expected = listOf(
            Point(1, 1),
            Point(4, 1),
            Point(4, 3),
            Point(2, 3),
        )
        assertEquals(expected, path.simplify())
    }

    @Test
    fun threeSegments_simplified() {
        /////////////////////
        //   1   2   3   4 //
        // 1 ════════════╗ //
        // 2             ║ //
        // 3     ════════╝ //
        /////////////////////
        val path = listOf(
            Point(1, 1),
            Point(4, 1),
            Point(4, 3),
            Point(2, 3),
        )
        val expected = listOf(
            Point(1, 1),
            Point(4, 1),
            Point(4, 3),
            Point(2, 3),
        )
        assertEquals(expected, path.simplify())
    }

    @Test
    fun threeSegments_partly_simplified() {
        /////////////////////
        //   1   2   3   4 //
        // 1 ════════════╗ //
        // 2             ║ //
        // 3     ════════╝ //
        /////////////////////
        val path = listOf(
            Point(1, 1),
            Point(2, 1),
            Point(4, 1),
            Point(4, 3),
            Point(2, 3),
        )
        val expected = listOf(
            Point(1, 1),
            Point(4, 1),
            Point(4, 3),
            Point(2, 3),
        )
        assertEquals(expected, path.simplify())
    }

    @Test(expected = IllegalStateException::class)
    fun threeSegments_impossible() {
        val path = listOf(
            Point(1, 1),
            Point(4, 1),
            Point(3, 3),
            Point(2, 3),
        )
        path.simplify()
    }
}
