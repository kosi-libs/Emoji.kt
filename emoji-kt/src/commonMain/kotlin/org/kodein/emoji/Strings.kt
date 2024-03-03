package org.kodein.emoji


public class EmojiTemplateCatalog(emojiList: List<Emoji>, builder: Builder.() -> Unit = {}) {
    private val aliases: Map<String, Emoji>
    private val emoticons: Map<String, Emoji>

    public class Builder internal constructor(private val aliases: MutableMap<String, Emoji>, private val emoticons: MutableMap<String, Emoji>) {
        public fun addAlias(alias: String, emoji: Emoji) { aliases.put(alias, emoji) }
        public fun addEmoticon(emoticon: String, emoji: Emoji) { emoticons.put(emoticon, emoji) }
    }

    init {
        val aliasMap: HashMap<String, Emoji> = if (emojiList.size == emojiCount) HashMap(emojiAliasCount) else HashMap()
        val emoticonMap: HashMap<String, Emoji> = if (emojiList.size == emojiCount) HashMap(emojiEmoticonCount) else HashMap()

        emojiList.forEach { emoji ->
            emoji.details.aliases.forEach { alias ->
                aliasMap[alias] = emoji
            }
            emoji.details.emoticons.forEach { emoticon ->
                emoticonMap[emoticon] = emoji
            }
        }

        Builder(aliasMap, emoticonMap).builder()

        aliases = aliasMap
        emoticons = emoticonMap
    }

    private val aliasRegex = Regex(":(?<alias>[a-zA-Z0-9_-]+)(~(?<tone1>[a-zA-Z-]+)(,(?<tone2>[a-zA-Z-]+))?)?:")

    /**
     * Replaces all short codes in the form of :shortcode: by their corresponding emoji.
     *
     * Some emojis can be skin toned, and can be short-coded as such: :alias~tone: or :alias~tone1,tone2:.
     * Tones can be 'light', 'medium-light', 'medium', 'medium-dark' or 'dark'.
     *
     * @receiver The string containing short codes.
     * @param aliases The alias map created with [aliasMap].
     */
    public fun replaceShortcodes(string: String): String =
        aliasRegex.replace(string) { match ->
            val alias = match.groups["alias"]?.value ?: return@replace match.value
            val emoji = aliases[alias] ?: return@replace match.value

            val tone1 = match.groups["tone1"]?.value?.let { SkinTone.fromAlias(it) }
            val tone2 = match.groups["tone2"]?.value?.let { SkinTone.fromAlias(it) }

            when {
                tone1 == null && tone2 == null -> emoji.toString()
                tone1 != null && tone2 == null -> (emoji as? SkinTone1Emoji)?.withSkinTone(tone1)?.toString() ?: match.value
                tone1 != null && tone2 != null -> (emoji as? SkinTone2Emoji)?.withSkinTone(tone1, tone2)?.toString() ?: match.value
                else -> match.value
            }
        }

    public fun replaceEmoticons(string: String): String {
        var result = string
        emoticons.forEach { (emoticon, emoji) ->
            result = result.replace(emoticon, emoji.toString())
        }
        return result
    }

    public fun replace(string: String): String = replaceEmoticons(replaceShortcodes(string))
}

/**
 * Creates a map from the list of emoji mapping their alias short-codes to their corresponding emoji.
 *
 * The Receiver .
 *
 * ```kotlin
 * val allEmoji = Emoji.all()
 * val aliases = allEmoji.aliasMap()
 * ```
 *
 * WARNING: This can be quite heavy to construct.
 * This method should be called in background and its result should be cached.
 *
 * @receiver Emoji list that should be created with [Emoji.Companion.all].
 */
public fun List<Emoji>.aliasMap(): Map<String, Emoji> {
    val map: HashMap<String, Emoji> = if (this.size == emojiCount) HashMap(emojiAliasCount) else HashMap()

    this.forEach { emoji ->
        emoji.details.aliases.forEach { alias ->
            map[alias] = emoji
        }
    }

    return map
}
