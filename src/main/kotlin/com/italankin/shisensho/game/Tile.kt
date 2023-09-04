package com.italankin.shisensho.game

import com.italankin.shisensho.game.util.manhattan
import java.util.concurrent.atomic.AtomicInteger

typealias TileId = Int

private val ID_GENERATOR = AtomicInteger(0)

class Tile(
    val id: TileId = ID_GENERATOR.getAndIncrement(),
    x: Int,
    y: Int,
    val value: TileValue,
    layer: Int = 0
) {

    var x: Int = x
        internal set

    var y: Int = y
        internal set

    var layer: Int = layer
        internal set

    internal var tileValueMatcher: TileValueMatcher = EqTileValueMatcher

    internal fun set(newX: Int, newY: Int) {
        x = newX
        y = newY
    }

    fun valueMatches(another: Tile): Boolean {
        return tileValueMatcher.matches(this.value, another.value)
    }

    fun distanceTo(another: Tile): Int {
        return manhattan(this.x, this.y, another.x, another.y)
    }

    fun copy(): Tile {
        val tile = Tile(
            id = this.id,
            x = this.x,
            y = this.y,
            value = this.value,
            layer = this.layer
        )
        tile.tileValueMatcher = tileValueMatcher
        return tile
    }

    override fun equals(other: Any?): Boolean {
        return other is Tile && this.id == other.id
    }

    override fun hashCode(): Int = id

    override fun toString() = "${value.c}($id)[$x;$y]"
}
