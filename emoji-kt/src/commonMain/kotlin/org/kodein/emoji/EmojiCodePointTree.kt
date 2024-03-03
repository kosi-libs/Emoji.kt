package org.kodein.emoji


public class EmojiFinder {

    internal class Node(
        var emoji: Emoji? = null,
        var branches: MutableMap<Int, Node>? = null
    )

    internal val root = Node()

    init { addAllEmoji() }

    /**
     * Adds the emoji to the tree at the corresponding code.
     *
     * @param codes The Unicode suite of code defining this emoji
     * @param emoji The emoji to register at the code
     */
    internal fun add(codes: IntArray, emoji: Emoji) {
        var node = root
        codes.forEach { code ->
            val map = node.branches ?: HashMap<Int, Node>().also { node.branches = it }
            node = map.getOrPut(code) { Node() }
        }
        check(node.emoji == null) { "Code ${codes.joinToString(" ")} is already set in tree to emoji ${node.emoji}" }
        node.emoji = emoji
    }

    /**
     * Adds all skin tone variations of this emoji to the tree at their corresponding code.
     * The original emoji is NOT added and needs to be added on its own with [add]
     *
     * @param template The Unicode template defining this emoji, skinTone indices will be overwritten.
     * @param emoji The emoji to register at the code with skin tone variations
     * @param sk1i The index of the skin tone code in [code]
     */
    internal fun addVariations(template: IntArray, emoji: SkinTone1Emoji, sk1i: Int) {
        SkinTone.entries.forEach { tone ->
            template[sk1i] = tone.codePoint
            add(template, emoji.withSkinTone(tone))
        }
    }

    /**
     * Adds all skin tone variations of this emoji to the tree at their corresponding code.
     * The original emoji is NOT added and needs to be added on its own with [add]
     *
     * @param template The Unicode template defining this emoji, skinTone indices will be overwritten.
     * @param emoji The emoji to register at the code with skin tone variations
     * @param sk21i The index of the first skin tone code in [code]
     * @param sk22i The index of the second skin tone code in [code]
     */
    internal fun addVariations(template: IntArray, emoji: SkinTone2Emoji, sk21i: Int, sk22i: Int) {
        SkinTone.entries.forEach { tone1 ->
            template[sk21i] = tone1.codePoint
            SkinTone.entries.forEach { tone2 ->
                template[sk22i] = tone2.codePoint
                add(template, emoji.withSkinTone(tone1, tone2))
            }
        }
    }
}

public data class FoundEmoji(
    val start: Int,
    val end: Int,
    val emoji: Emoji
) {
    public val length: Int get() = end - start
}

private tailrec fun follow(string: String, index: Int, node: EmojiFinder.Node, start: Int): FoundEmoji? {
    if (index >= string.length) return node.emoji?.let { FoundEmoji(start, index, it) }
    val branches = node.branches ?: return node.emoji?.let { FoundEmoji(start, index, it) }
    val code = codePointAt(string, index)
    val next = branches[code] ?: return node.emoji?.let { FoundEmoji(start, index, it) }
    return follow(
        string = string,
        index = index + codePointCharLength(code),
        node = next,
        start = start,
    )
}

/**
 * Finds all emojis inside a String and returns their position and details.
 */
public fun EmojiFinder.findEmoji(str: String): Sequence<FoundEmoji> =
    sequence {
        var index = 0
        while (index < str.length) {
            val found = follow(str, index, root, index)
            if (found != null) {
                yield(found)
                index += found.length
            } else {
                index += codePointCharLength(codePointAt(str, index))
            }
        }
    }
