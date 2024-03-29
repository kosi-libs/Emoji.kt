package org.kodein.emoji.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.em
import kotlinx.coroutines.launch
import org.kodein.emoji.Emoji
import org.kodein.emoji.FoundEmoji
import org.kodein.emoji.findEmoji


internal expect fun AnnotatedString.Builder.appendNotoPlaceholder(emoji: Emoji, inlineContent: MutableMap<String, InlineTextContent>)

@Composable
public fun String.withEmoji(): String {
    val service = EmojiService.get() ?: return ""
    return remember(this) { service.catalog.replace(this) }
}

@Composable
private fun WithNotoEmoji(
    text: CharSequence,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit,
    createInlineTextContent: suspend (FoundEmoji) -> InlineTextContent?
) {
    val service = EmojiService.get() ?: return

    val all = remember(text) {
        service.finder.findEmoji(text)
            .map { found ->
                found to mutableStateOf<InlineTextContent?>(null)
            }
            .toList()
    }

    LaunchedEffect(all) {
        all.forEach { (found, inlineTextContent) ->
            launch {
                inlineTextContent.value = createInlineTextContent(found)
            }
        }
    }

    val inlineContent = HashMap<String, InlineTextContent>()
    val annotatedString = buildAnnotatedString {
        var start = 0
        all.forEach { (found, inlineTextContent) ->
            if (text is AnnotatedString)
                append(text.subSequence(start, found.start))
            else
                append(text.substring(start, found.start))
            val itc = inlineTextContent.value
            if (itc != null) {
                val inlineContentID = "emoji:${found.emoji}"
                inlineContent[inlineContentID] = itc
                appendInlineContent(inlineContentID)
            } else {
                appendNotoPlaceholder(found.emoji, inlineContent)
            }
            start = found.end
        }
        if (text is AnnotatedString)
            append(text.subSequence(start, text.length))
        else
            append(text.substring(start, text.length))
    }

    content(annotatedString, inlineContent)
}

private suspend fun createNotoSvgInlineContent(emoji: Emoji, download: suspend (EmojiUrl) -> ByteArray): InlineTextContent? {
    try {
        val bytes = download(EmojiUrl.from(emoji, EmojiUrl.Type.SVG))
        val svg = SVGImage.create(bytes)
        return InlineTextContent(
            placeholder = Placeholder(1.em, 1.em / svg.sizeRatio(), PlaceholderVerticalAlign.Center),
            children = {
                SVGImage(svg, "${emoji.details.description} emoji", Modifier.fillMaxSize())
            }
        )
    } catch (t: Throwable) {
        println("${t::class.simpleName}: ${t.message}")
        return null
    }
}

@Composable
public fun WithNotoImageEmoji(
    text: CharSequence,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
) {
    val download = LocalEmojiDownloader.current
    WithNotoEmoji(
        text = text,
        content = content,
        createInlineTextContent = { found -> createNotoSvgInlineContent(found.emoji, download) }
    )
}

private suspend fun createNotoLottieInlineContent(emoji: Emoji, download: suspend (EmojiUrl) -> ByteArray): InlineTextContent? {
    if (!emoji.details.notoAnimated) return createNotoSvgInlineContent(emoji, download)
    try {
        val bytes = download(EmojiUrl.from(emoji, EmojiUrl.Type.Lottie))
        val animation = LottieAnimation.create(bytes)
        return InlineTextContent(
            placeholder = Placeholder(1.em, 1.em / animation.sizeRatio(), PlaceholderVerticalAlign.Center),
            children = {
                LottieAnimation(animation, "${emoji.details.description} emoji", Modifier.fillMaxSize())
            }
        )
    } catch (t: Throwable) {
        println("${t::class.simpleName}: ${t.message}")
        return createNotoSvgInlineContent(emoji, download)
    }
}

@Composable
public fun WithNotoAnimatedEmoji(
    text: CharSequence,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
) {
    val download = LocalEmojiDownloader.current
    WithNotoEmoji(
        text = text,
        content = content,
        createInlineTextContent = { found -> createNotoLottieInlineContent(found.emoji, download) }
    )
}

@Composable
public expect fun WithPlatformEmoji(
    text: CharSequence,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
)
