package com.italankin.shisensho.game

class Move(
    val a: Tile,
    val b: Tile,
    val path: Path
) {

    /**
     * @return whether this move starts or ends with [tile]
     */
    operator fun contains(tile: Tile): Boolean {
        return a == tile || b == tile
    }

    override fun toString(): String {
        return "$a -> $b: $path"
    }
}
