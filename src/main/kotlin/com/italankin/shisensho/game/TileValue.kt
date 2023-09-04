package com.italankin.shisensho.game

enum class TileValue(
    /**
     * Unicode string representation
     */
    val s: String,
    /**
     * Char mapping
     */
    val c: Char
) {

    DOTS_1("🀙", 'A'), // 1F019 🀙 MAHJONG TILE ONE OF CIRCLES
    DOTS_2("🀚", 'B'), // 1F01A 🀚 MAHJONG TILE TWO OF CIRCLES
    DOTS_3("🀛", 'C'), // 1F01B 🀛 MAHJONG TILE THREE OF CIRCLES
    DOTS_4("🀜", 'D'), // 1F01C 🀜 MAHJONG TILE FOUR OF CIRCLES
    DOTS_5("🀝", 'E'), // 1F01D 🀝 MAHJONG TILE FIVE OF CIRCLES
    DOTS_6("🀞", 'F'), // 1F01E 🀞 MAHJONG TILE SIX OF CIRCLES
    DOTS_7("🀟", 'G'), // 1F01F 🀟 MAHJONG TILE SEVEN OF CIRCLES
    DOTS_8("🀠", 'H'), // 1F020 🀠 MAHJONG TILE EIGHT OF CIRCLES
    DOTS_9("🀡", 'I'), // 1F021 🀡 MAHJONG TILE NINE OF CIRCLES

    BAM_1("🀐", 'J'), // 1F010 🀐 MAHJONG TILE ONE OF BAMBOOS
    BAM_2("🀑", 'K'), // 1F011 🀑 MAHJONG TILE TWO OF BAMBOOS
    BAM_3("🀒", 'L'), // 1F012 🀒 MAHJONG TILE THREE OF BAMBOOS
    BAM_4("🀓", 'M'), // 1F013 🀓 MAHJONG TILE FOUR OF BAMBOOS
    BAM_5("🀔", 'N'), // 1F014 🀔 MAHJONG TILE FIVE OF BAMBOOS
    BAM_6("🀕", 'O'), // 1F015 🀕 MAHJONG TILE SIX OF BAMBOOS
    BAM_7("🀖", 'P'), // 1F016 🀖 MAHJONG TILE SEVEN OF BAMBOOS
    BAM_8("🀗", 'Q'), // 1F017 🀗 MAHJONG TILE EIGHT OF BAMBOOS
    BAM_9("🀘", 'R'), // 1F018 🀘 MAHJONG TILE NINE OF BAMBOOS

    CHAR_1("🀇", 'S'), // 1F007 🀇 MAHJONG TILE ONE OF CHARACTERS
    CHAR_2("🀈", 'T'), // 1F008 🀈 MAHJONG TILE TWO OF CHARACTERS
    CHAR_3("🀉", 'U'), // 1F009 🀉 MAHJONG TILE THREE OF CHARACTERS
    CHAR_4("🀊", 'V'), // 1F00A 🀊 MAHJONG TILE FOUR OF CHARACTERS
    CHAR_5("🀋", 'W'), // 1F00B 🀋 MAHJONG TILE FIVE OF CHARACTERS
    CHAR_6("🀌", 'X'), // 1F00C 🀌 MAHJONG TILE SIX OF CHARACTERS
    CHAR_7("🀍", 'Y'), // 1F00D 🀍 MAHJONG TILE SEVEN OF CHARACTERS
    CHAR_8("🀎", 'Z'), // 1F00E 🀎 MAHJONG TILE EIGHT OF CHARACTERS
    CHAR_9("🀏", '0'), // 1F00F 🀏 MAHJONG TILE NINE OF CHARACTERS

    WIND_EAST("🀀", '1'), // 1F000 🀀 MAHJONG TILE EAST WIND
    WIND_SOUTH("🀁", '2'), // 1F001 🀁 MAHJONG TILE SOUTH WIND
    WIND_WEST("🀂", '3'), // 1F002 🀂 MAHJONG TILE WEST WIND
    WIND_NORTH("🀃", '4'), // 1F003 🀃 MAHJONG TILE NORTH WIND

    SEASON_SPRING("🀦", '5'), // 1F026 🀦 MAHJONG TILE SPRING
    SEASON_SUMMER("🀧", '6'), // 1F027 🀧 MAHJONG TILE SUMMER
    SEASON_AUTUMN("🀨", '7'), // 1F028 🀨 MAHJONG TILE AUTUMN
    SEASON_WINTER("🀩", '8'), // 1F029 🀩 MAHJONG TILE WINTER

    DRAGON_RED("🀄", '#'), // 1F004 🀄 MAHJONG TILE RED DRAGON
    DRAGON_GREEN("🀅", '%'), // 1F005 🀅 MAHJONG TILE GREEN DRAGON
    DRAGON_WHITE("🀆", ' '), // 1F006 🀆 MAHJONG TILE WHITE DRAGON

    FLOWER_PLUM("🀢", '@'), // 1F022 🀢 MAHJONG TILE PLUM
    FLOWER_ORCHID("🀣", '!'), // 1F023 🀣 MAHJONG TILE ORCHID
    FLOWER_BAMBOO("🀤", '?'), // 1F024 🀤 MAHJONG TILE BAMBOO
    FLOWER_MUM("🀥", '&'), // 1F025 🀥 MAHJONG TILE CHRYSANTHEMUM
    ;

    companion object {
        val DOTS = listOf(
            DOTS_1, DOTS_2, DOTS_3,
            DOTS_4, DOTS_5, DOTS_6,
            DOTS_7, DOTS_8, DOTS_9
        )
        val BAMBOOS = listOf(
            BAM_1, BAM_2, BAM_3,
            BAM_4, BAM_5, BAM_6,
            BAM_7, BAM_8, BAM_9,
        )
        val CHARACTERS = listOf(
            CHAR_1, CHAR_2, CHAR_3,
            CHAR_4, CHAR_5, CHAR_6,
            CHAR_7, CHAR_8, CHAR_9,
        )
        val WINDS = listOf(WIND_EAST, WIND_SOUTH, WIND_WEST, WIND_NORTH)
        val DRAGONS = listOf(DRAGON_RED, DRAGON_GREEN, DRAGON_WHITE)
        val SEASONS = listOf(SEASON_SPRING, SEASON_SUMMER, SEASON_AUTUMN, SEASON_WINTER)
        val SEASONS_SET = SEASONS.toSet()
        val FLOWERS = listOf(FLOWER_PLUM, FLOWER_ORCHID, FLOWER_MUM, FLOWER_BAMBOO)
        val FLOWERS_SET = FLOWERS.toSet()

        fun createAlphabet(pairsCount: Int, chineseStyle: Boolean = false): List<TileValue> {
            if (pairsCount < 1) {
                throw IllegalArgumentException("pairsCount < 1: $pairsCount")
            }
            val result = ArrayList<TileValue>(values().size * pairsCount * 2)
            for (v in DOTS) {
                repeat(pairsCount * 2) { result.add(v) }
            }
            for (v in BAMBOOS) {
                repeat(pairsCount * 2) { result.add(v) }
            }
            for (v in CHARACTERS) {
                repeat(pairsCount * 2) { result.add(v) }
            }
            for (v in WINDS) {
                repeat(pairsCount * 2) { result.add(v) }
            }
            for (v in DRAGONS) {
                repeat(pairsCount * 2) { result.add(v) }
            }
            if (chineseStyle) {
                result.addAll(SEASONS)
                result.addAll(FLOWERS)
            } else {
                for (v in SEASONS) {
                    repeat(pairsCount * 2) { result.add(v) }
                }
                for (v in FLOWERS) {
                    repeat(pairsCount * 2) { result.add(v) }
                }
            }
            return result
        }
    }
}

/**
 * Map [Char]s to [TileValue]s by [TileValue.c]
 */
fun List<Char?>.toTileValues(): List<TileValue?> {
    val byChar = TileValue.values().associateBy(TileValue::c)
    return map { c ->
        c?.let { byChar[c] ?: throw IllegalArgumentException("invalid char: '$c'") }
    }
}
