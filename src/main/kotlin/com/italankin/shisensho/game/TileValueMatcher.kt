package com.italankin.shisensho.game

sealed interface TileValueMatcher {

    companion object {
        operator fun invoke(chineseStyle: Boolean): TileValueMatcher {
            return if (chineseStyle) ChineseStyleTileValueMatcher else EqTileValueMatcher
        }
    }

    fun matches(t1: TileValue, t2: TileValue): Boolean
}

/**
 * Exact match by value
 */
object EqTileValueMatcher : TileValueMatcher {

    override fun matches(t1: TileValue, t2: TileValue): Boolean {
        return t1 == t2
    }
}

/**
 * Any season match any season, any flower match any flower
 */
object ChineseStyleTileValueMatcher : TileValueMatcher {

    override fun matches(t1: TileValue, t2: TileValue): Boolean {
        return t1 in TileValue.FLOWERS_SET && t2 in TileValue.FLOWERS_SET ||
                t1 in TileValue.SEASONS_SET && t2 in TileValue.SEASONS_SET
                || t1 == t2
    }
}
