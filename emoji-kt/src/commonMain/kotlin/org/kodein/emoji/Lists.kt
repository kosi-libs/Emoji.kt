package org.kodein.emoji


internal fun MutableList<Emoji>.addSt1Variations(emoji: SkinTone1Emoji) {
    SkinTone.entries.forEach { tone ->
        add(emoji.withSkinTone(tone))
    }
}

internal fun MutableList<Emoji>.addSt2Variations(emoji: SkinTone2Emoji) {
    SkinTone.entries.forEach { tone1 ->
        SkinTone.entries.forEach { tone2 ->
            add(emoji.withSkinTone(tone1, tone2))
        }
    }
}
