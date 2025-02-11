package org.kodein.emoji.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.em
import org.kodein.emoji.Emoji
import org.kodein.emoji.findEmoji


internal expect fun AnnotatedString.Builder.appendNotoPlaceholder(emoji: Emoji, inlineContent: MutableMap<String, InlineTextContent>)

/**
 * Replaces all shortcodes (i.e. :emoji: or :emoji~skintone:) with their actual corresponding emojis.
 */
@Composable
public fun String.withEmoji(): String {
    val service = EmojiService.get() ?: return ""
    return remember(this) { service.catalog.replace(this) }
}

@Composable
private fun WithNotoEmoji(
    text: CharSequence,
    ratio: Emoji.() -> Float,
    placeholder: @Composable (Emoji) -> Unit,
    createDisplay: suspend (Emoji) -> (@Composable () -> Unit)?,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit,
) {
    val service = EmojiService.get() ?: return

    val all = remember(text) { service.finder.findEmoji(text).toList() }

    val inlineContent = HashMap<String, InlineTextContent>()
    val annotatedString = buildAnnotatedString {
        var start = 0
        all.forEach { found ->
            if (text is AnnotatedString)
                append(text.subSequence(start, found.start))
            else
                append(text.substring(start, found.start))
            val inlineContentID = "emoji:${found.emoji}"
            inlineContent[inlineContentID] = InlineTextContent(Placeholder(found.emoji.ratio().em, 1.em, PlaceholderVerticalAlign.Center)) {
                val display by produceState<(@Composable () -> Unit)?>(null, found.emoji) {
                    value = createDisplay(found.emoji)
                }
                display?.invoke() ?: placeholder(found.emoji)
            }
            appendInlineContent(inlineContentID)
            start = found.end
        }
        if (text is AnnotatedString)
            append(text.subSequence(start, text.length))
        else
            append(text.substring(start, text.length))
    }

    content(annotatedString, inlineContent)
}

private suspend fun createNotoSvgContent(emoji: Emoji, download: suspend (EmojiUrl) -> ByteArray): (@Composable () -> Unit)? {
    try {
        val bytes = download(EmojiUrl.from(emoji, EmojiUrl.Type.SVG))
        val svg = SVGImage.create(bytes)
        return {
            SVGImage(svg, "${emoji.details.description} emoji", Modifier.fillMaxSize())
        }
    } catch (t: Throwable) {
        println("${t::class.simpleName}: ${t.message}")
        return null
    }
}

/**
 * Creates an annotated String and a `InlineTextContent` map from a text containing Emoji characters.
 * Replaces all emojis with [NotoImageEmoji].
 *
 * @param text The text to with Emoji UTF characters.
 * @param content A lambda that receives the `AnnotatedString` and its corresponding `InlineTextContent` map
 *                These should be used to display: `{ astr, map -> Text(astr, inlineContent = map) }`.
 */
@Composable
public fun WithNotoImageEmoji(
    text: CharSequence,
    placeholder: @Composable (Emoji) -> Unit = { PlatformEmojiPlaceholder(it, Modifier.fillMaxSize()) },
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
) {
    val download = LocalEmojiDownloader.current
    WithNotoEmoji(
        text = text,
        ratio = {
            details.notoImageRatio.takeIf { it > 0f }
                ?: 1f
        },
        placeholder = placeholder,
        createDisplay = { emoji -> createNotoSvgContent(emoji, download) },
        content = content
    )
}

@Deprecated("fixedSize is now ignored (size ratio is now part of emoji details)")
@Composable
public fun WithNotoImageEmoji(
    text: CharSequence,
    fixedSize: Boolean,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
): Unit = WithNotoImageEmoji(text = text, content = content)


private suspend fun createNotoLottieContent(
    emoji: Emoji,
    iterations: Int,
    speed: Float,
    download: suspend (EmojiUrl) -> ByteArray
): (@Composable () -> Unit)? {
    if (!emoji.details.hasNotoAnimation) return createNotoSvgContent(emoji, download)
    try {
        val bytes = download(EmojiUrl.from(emoji, EmojiUrl.Type.Lottie))
        val animation = LottieAnimation.create(bytes)
        return {
            LottieAnimation(animation, iterations, 1f, speed, "${emoji.details.description} emoji", Modifier.fillMaxSize())
        }
    } catch (t: Throwable) {
        println("${t::class.simpleName}: ${t.message}")
        return createNotoSvgContent(emoji, download)
    }
}

/**
 * Creates an annotated String and a `InlineTextContent` map from a text containing Emoji characters.
 * Replaces all emojis with [NotoAnimatedEmoji].
 *
 * @param text The text to with Emoji UTF characters.
 * @param iterations The number of times that the animations will be played (default is infinite).
 * @param speed Speed at which the animations will be rendered.
 * @param content A lambda that receives the `AnnotatedString` and its corresponding `InlineTextContent` map
 *                These should be used to display: `{ astr, map -> Text(astr, inlineContent = map) }`.
 */
@Composable
public fun WithNotoAnimatedEmoji(
    text: CharSequence,
    iterations: Int = Int.MAX_VALUE,
    speed: Float = 1f,
    placeholder: @Composable (Emoji) -> Unit = { PlatformEmojiPlaceholder(it, Modifier.fillMaxSize()) },
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
) {
    val download = LocalEmojiDownloader.current
    WithNotoEmoji(
        text = text,
        ratio = {
            details.notoAnimationRatio.takeIf { it > 0f }
                ?: details.notoImageRatio.takeIf { it > 0f }
                ?: 1f
        },
        placeholder = placeholder,
        createDisplay = { emoji -> createNotoLottieContent(emoji, iterations, speed, download) },
        content = content,
    )
}

@Deprecated("fixedSize is now ignored (size ratio is now part of emoji details)")
@Composable
public fun WithNotoAnimatedEmoji(
    text: CharSequence,
    iterations: Int = Int.MAX_VALUE,
    speed: Float = 1f,
    fixedSize: Boolean,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
): Unit = WithNotoAnimatedEmoji(text = text, iterations = iterations, speed = speed, content = content)
/**
 * Creates an annotated String and a `InlineTextContent` map from a text containing Emoji characters.
 *
 * - On Wasm: Replaces all emojis with [NotoImageEmoji].
 * - On all other platforms: does not modify the text at all (map will be empty).
 *
 * @param text The text to with Emoji UTF characters.
 * @param fixedImageSize If true, then the emoji will not be resized once downloaded.
 * @param content A lambda that receives the `AnnotatedString` and its corresponding `InlineTextContent` map
 *                These should be used to display: `{ astr, map -> Text(astr, inlineContent = map) }`.
 */
@Composable
public expect fun WithPlatformEmoji(
    text: CharSequence,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
)

@Deprecated("fixedSize is now ignored (size ratio is now part of emoji details)")
@Composable
public fun WithPlatformEmoji(
    text: CharSequence,
    fixedImageSize: Boolean,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
): Unit = WithPlatformEmoji(text, content)
