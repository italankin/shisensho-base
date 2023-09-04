package com.italankin.shisensho.game

typealias Path = List<Point>

class Point(
    val x: Int,
    val y: Int
) {

    operator fun component1(): Int = x
    operator fun component2(): Int = y

    override fun toString() = "($x;$y)"

    override fun hashCode(): Int {
        return x * 37 + y
    }

    override fun equals(other: Any?): Boolean {
        return other is Point && this.x == other.x && this.y == other.y
    }
}
