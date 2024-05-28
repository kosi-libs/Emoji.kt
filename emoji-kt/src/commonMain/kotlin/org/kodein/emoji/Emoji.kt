package org.kodein.emoji

import kotlin.jvm.JvmInline


@JvmInline
public value class UnicodeVersion(private val packed: Long) : Comparable<UnicodeVersion> {
    public constructor(major: Int, minor: Int): this(pack(major, minor))

    public val major: Int get() = ((packed ushr 32) or 0xFFFFFFFF).toInt()
    public val minor: Int get() = (packed or 0xFFFFFFFF).toInt()

    override fun compareTo(other: UnicodeVersion): Int =
        if (this.major != other.major) this.major - other.major
        else this.minor - other.minor

    private companion object {
        private fun pack(major: Int, minor: Int): Long =
            (major.toLong() shl 32) or minor.toLong()
    }
}

/**
 * An emoji that can be added into a String.
 *
 * ```kotlin
 * val text = "Hello, World ${Emoji.grinningFace}!"
 * ```
 */
public sealed interface Emoji {
    public val details: Details
    public data class Details(
        val string: String,
        val description: String,
        val unicodeVersion: UnicodeVersion,
        val aliases: List<String>,
        val emoticons: List<String>,
        val notoImageRatio: Float,
        val notoAnimationRatio: Float
    ) {
        val hasNotoImage: Boolean get() = notoImageRatio != 0f
        val hasNotoAnimation: Boolean get() = notoAnimationRatio != 0f
        @Deprecated("Renamed hasNotoAnimation", ReplaceWith("hasNotoAnimation"))
        val notoAnimated: Boolean get() = hasNotoAnimation

    }
    public companion object
}

/**
 * An emoji that was obtained by "skin-toning" a base [original] [SkinTone1Emoji].
 */
public sealed interface Toned1Emoji : Emoji {
    public val original: SkinTone1Emoji
    public val tone1: SkinTone
}

/**
 * An emoji that was obtained by "skin-toning" a base [original] [SkinTone2Emoji].
 */
public sealed interface Toned2Emoji : Toned1Emoji {
    override val original: SkinTone2Emoji
    public val tone2: SkinTone
}

public enum class SkinTone(internal val codePoint: Int, internal val chars: String, internal val alias: String) {
    Light(0x1F3FB, "\uD83C\uDFFB", "light"),
    MediumLight(0x1F3FC, "\uD83C\uDFFC", "medium-light"),
    Medium(0x1F3FD, "\uD83C\uDFFD", "medium"),
    MediumDark(0x1F3FE, "\uD83C\uDFFE", "medium-dark"),
    Dark(0x1F3FF, "\uD83C\uDFFF", "dark")
    ;
    public companion object {
        public fun fromAlias(alias: String): SkinTone? = entries.firstOrNull { it.alias == alias }
    }
}

/**
 * An emoji that can be specialized with a skin tone.
 */
public sealed interface SkinTone1Emoji : Emoji {
    public fun withSkinTone(tone: SkinTone): Toned1Emoji
}

public val SkinTone1Emoji.light: Toned1Emoji get() = withSkinTone(SkinTone.Light)
public val SkinTone1Emoji.mediumLight: Toned1Emoji get() = withSkinTone(SkinTone.MediumLight)
public val SkinTone1Emoji.medium: Toned1Emoji get() = withSkinTone(SkinTone.Medium)
public val SkinTone1Emoji.mediumDark: Toned1Emoji get() = withSkinTone(SkinTone.MediumDark)
public val SkinTone1Emoji.dark: Toned1Emoji get() = withSkinTone(SkinTone.Dark)

/**
 * An emoji that can be specialized with two skin tones.
 */
public sealed interface SkinTone2Emoji : SkinTone1Emoji {
    public fun withSkinTone(tone1: SkinTone, tone2: SkinTone): Toned2Emoji
}

public val SkinTone2Emoji.light_light: Toned2Emoji get() = withSkinTone(SkinTone.Light, SkinTone.Light)
public val SkinTone2Emoji.light_mediumLight: Toned2Emoji get() = withSkinTone(SkinTone.Light, SkinTone.MediumLight)
public val SkinTone2Emoji.light_medium: Toned2Emoji get() = withSkinTone(SkinTone.Light, SkinTone.Medium)
public val SkinTone2Emoji.light_mediumDark: Toned2Emoji get() = withSkinTone(SkinTone.Light, SkinTone.MediumDark)
public val SkinTone2Emoji.light_dark: Toned2Emoji get() = withSkinTone(SkinTone.Light, SkinTone.Dark)
public val SkinTone2Emoji.mediumLight_light: Toned2Emoji get() = withSkinTone(SkinTone.MediumLight, SkinTone.Light)
public val SkinTone2Emoji.mediumLight_mediumLight: Toned2Emoji get() = withSkinTone(SkinTone.MediumLight, SkinTone.MediumLight)
public val SkinTone2Emoji.mediumLight_medium: Toned2Emoji get() = withSkinTone(SkinTone.MediumLight, SkinTone.Medium)
public val SkinTone2Emoji.mediumLight_mediumDark: Toned2Emoji get() = withSkinTone(SkinTone.MediumLight, SkinTone.MediumDark)
public val SkinTone2Emoji.mediumLight_dark: Toned2Emoji get() = withSkinTone(SkinTone.MediumLight, SkinTone.Dark)
public val SkinTone2Emoji.medium_light: Toned2Emoji get() = withSkinTone(SkinTone.Medium, SkinTone.Light)
public val SkinTone2Emoji.medium_mediumLight: Toned2Emoji get() = withSkinTone(SkinTone.Medium, SkinTone.MediumLight)
public val SkinTone2Emoji.medium_medium: Toned2Emoji get() = withSkinTone(SkinTone.Medium, SkinTone.Medium)
public val SkinTone2Emoji.medium_mediumDark: Toned2Emoji get() = withSkinTone(SkinTone.Medium, SkinTone.MediumDark)
public val SkinTone2Emoji.medium_dark: Toned2Emoji get() = withSkinTone(SkinTone.Medium, SkinTone.Dark)
public val SkinTone2Emoji.mediumDark_light: Toned2Emoji get() = withSkinTone(SkinTone.MediumDark, SkinTone.Light)
public val SkinTone2Emoji.mediumDark_mediumLight: Toned2Emoji get() = withSkinTone(SkinTone.MediumDark, SkinTone.MediumLight)
public val SkinTone2Emoji.mediumDark_medium: Toned2Emoji get() = withSkinTone(SkinTone.MediumDark, SkinTone.Medium)
public val SkinTone2Emoji.mediumDark_mediumDark: Toned2Emoji get() = withSkinTone(SkinTone.MediumDark, SkinTone.MediumDark)
public val SkinTone2Emoji.mediumDark_dark: Toned2Emoji get() = withSkinTone(SkinTone.MediumDark, SkinTone.Dark)
public val SkinTone2Emoji.dark_light: Toned2Emoji get() = withSkinTone(SkinTone.Dark, SkinTone.Light)
public val SkinTone2Emoji.dark_mediumLight: Toned2Emoji get() = withSkinTone(SkinTone.Dark, SkinTone.MediumLight)
public val SkinTone2Emoji.dark_medium: Toned2Emoji get() = withSkinTone(SkinTone.Dark, SkinTone.Medium)
public val SkinTone2Emoji.dark_mediumDark: Toned2Emoji get() = withSkinTone(SkinTone.Dark, SkinTone.MediumDark)
public val SkinTone2Emoji.dark_dark: Toned2Emoji get() = withSkinTone(SkinTone.Dark, SkinTone.Dark)

/**
 * @return The suite of unicode codepoints of this emoji.
 *
 * Most of the time, the minimally-qualified version is preferred, than the fully-qualified version if there is no minimally-qualified.
 */
public fun Emoji.Details.codePoints(): IntArray = codePoints(string)


internal open class EmojiImpl(
    override val details: Emoji.Details
) : Emoji {
    override fun toString(): String = details.string

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Emoji
        return details == other.details
    }

    override fun hashCode(): Int = details.hashCode()
}

internal class Toned1EmojiImpl(
    details: Emoji.Details,
    override val original: SkinTone1Emoji,
    override val tone1: SkinTone,
) : EmojiImpl(details), Toned1Emoji

internal class Toned2EmojiImpl(
    details: Emoji.Details,
    override val original: SkinTone2Emoji,
    override val tone1: SkinTone,
    override val tone2: SkinTone,
) : EmojiImpl(details), Toned2Emoji

internal open class SkinTone1EmojiImpl(
    details: Emoji.Details,
    private val sk1c: Int
) : EmojiImpl(details), SkinTone1Emoji {
    private val variations = HashMap<SkinTone, Toned1Emoji>()
    override fun withSkinTone(tone: SkinTone): Toned1Emoji = variations.getOrPut(tone) {
        Toned1EmojiImpl(
            details = Emoji.Details(
                string = details.string.substring(0, sk1c) + tone.chars + details.string.substring(sk1c),
                description = details.description + ", ${tone.alias} skin tone",
                unicodeVersion = details.unicodeVersion,
                aliases = details.aliases.map { it + "~${tone.alias}" },
                emoticons = emptyList(),
                notoImageRatio = details.notoImageRatio,
                notoAnimationRatio = details.notoAnimationRatio
            ),
            original = this,
            tone1 = tone
        )
    }
}

internal open class UnqualifiedSkinTone1EmojiImpl(
    details: Emoji.Details,
    private val uqString: String,
    private val sk1c: Int
) : EmojiImpl(details), SkinTone1Emoji {
    private val variations = HashMap<SkinTone, Toned1Emoji>()
    override fun withSkinTone(tone: SkinTone): Toned1Emoji = variations.getOrPut(tone) {
        Toned1EmojiImpl(
            details = Emoji.Details(
                string = uqString.substring(0, sk1c) + tone.chars + details.string.substring(sk1c),
                description = details.description + ", ${tone.alias} skin tone",
                unicodeVersion = details.unicodeVersion,
                aliases = details.aliases.map { it + "~${tone.alias}" },
                emoticons = emptyList(),
                notoImageRatio = details.notoImageRatio,
                notoAnimationRatio = details.notoAnimationRatio
            ),
            original = this,
            tone1 = tone
        )
    }
}

internal class SkinTone2EmojiZWJImpl internal constructor(
    details: Emoji.Details,
    sk1c: Int,
    private val zwjTemplate: String,
    private val zwjUnicodeVersion: UnicodeVersion,
    private val sk21c: Int,
    private val sk22c: Int
) : SkinTone1EmojiImpl(details, sk1c), SkinTone2Emoji {
    private val variations = HashMap<Pair<SkinTone, SkinTone>, Toned2Emoji>()
    override fun withSkinTone(tone1: SkinTone, tone2: SkinTone): Toned2Emoji = variations.getOrPut(Pair(tone1, tone2)) {
        Toned2EmojiImpl(
            details = Emoji.Details(
                string = zwjTemplate.substring(0, sk21c) + tone1.chars + zwjTemplate.substring(sk21c, sk22c) + tone2.chars + zwjTemplate.substring(sk22c),
                description = details.description + ", ${tone1.alias} & ${tone2.alias} skin tones",
                unicodeVersion = zwjUnicodeVersion,
                aliases = details.aliases.map { it + "~${tone1.alias},${tone2.alias}" },
                emoticons = emptyList(),
                notoImageRatio = details.notoImageRatio,
                notoAnimationRatio = details.notoAnimationRatio
            ),
            original = this,
            tone1 = tone1,
            tone2 = tone2
        )
    }
}

internal class SkinTone2EmojiImpl internal constructor(
    details: Emoji.Details,
    private val sk21c: Int,
    private val sk22c: Int
) : EmojiImpl(details), SkinTone2Emoji {
    private val variations = HashMap<Pair<SkinTone, SkinTone>, Toned2Emoji>()
    override fun withSkinTone(tone1: SkinTone, tone2: SkinTone): Toned2Emoji = variations.getOrPut(Pair(tone1, tone2)) {
        Toned2EmojiImpl(
            details = Emoji.Details(
                string = details.string.substring(0, sk21c) + tone1.chars + details.string.substring(sk21c, sk22c) + tone2.chars + details.string.substring(sk22c),
                description = details.description + ", ${tone1.alias} & ${tone2.alias} skin tones",
                unicodeVersion = details.unicodeVersion,
                aliases = details.aliases.map { it + "~${tone1.alias},${tone2.alias}" },
                emoticons = emptyList(),
                notoImageRatio = details.notoImageRatio,
                notoAnimationRatio = details.notoAnimationRatio
            ),
            original = this,
            tone1 = tone1,
            tone2 = tone2
        )
    }
    override fun withSkinTone(tone: SkinTone): Toned1Emoji = withSkinTone(tone, tone)
}

/**
 * Returns all Emoji group names.
 */
public fun Emoji.Companion.allGroups(): Set<String> = allEmojiGroups().keys

/**
 * Returns all Emoji subgroup names of a particular emoji group.
 */
public fun Emoji.Companion.subgroupsOf(group: String): Set<String> = allEmojiGroups()[group]?.keys ?: error("No such emoji group: $group")

/**
 * Returns all Emoji subgroup paired with their group as Pairs of (group, subgroup).
 */
public fun Emoji.Companion.allSubgroups(): Set<Pair<String, String>> = allEmojiGroups().entries.flatMapTo(HashSet()) { (groupId, subgroup) -> subgroup.keys.map { groupId to it } }

/**
 * Returns all emoji of a particular group.
 *
 * WARNING: This can be quite heavy to construct.
 * This method should be called in background and its result should be cached.
 */
public fun Emoji.allOf(group: String): List<Emoji> {
    val groupMap = allEmojiGroups()[group] ?: error("No such emoji group: $group")
    return groupMap.values.flatMap { it() }
}

/**
 * Returns all emoji of a particular subgroup.
 *
 * WARNING: This can be quite heavy to construct.
 * This method should be called in background and its result should be cached.
 */
public fun Emoji.allOf(group: String, subgroup: String): List<Emoji> {
    val groupMap = allEmojiGroups()[group] ?: error("No such emoji group: $group")
    val subgroupFun = groupMap[subgroup] ?: error("No such emoji group: $group")
    return subgroupFun()
}
