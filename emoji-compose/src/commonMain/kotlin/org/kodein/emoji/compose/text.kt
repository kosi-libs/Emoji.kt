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
    text: String,
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
        append(text.substring(start, text.length))
    }

    content(annotatedString, inlineContent)
}


@Composable
public fun WithNotoImageEmoji(
    text: String,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
) {
    val download = LocalEmojiDownloader.current
    WithNotoEmoji(
        text = text,
        content = content,
        createInlineTextContent = { found ->
            val bytes = download(EmojiUrl.from(found.emoji, EmojiUrl.Type.SVG)) ?: return@WithNotoEmoji null
            val svg = SVGImage.create(bytes)
            InlineTextContent(
                placeholder = Placeholder(1.em, 1.em / svg.sizeRatio(), PlaceholderVerticalAlign.Center),
                children = {
                    SVGImage(svg, "${found.emoji.details.description} emoji", Modifier.fillMaxSize())
                }
            )
        }
    )
}

@Composable
public fun WithNotoAnimatedEmoji(
    text: String,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
) {
    val download = LocalEmojiDownloader.current
    WithNotoEmoji(
        text = text,
        content = content,
        createInlineTextContent = { found ->
            val bytes = download(EmojiUrl.from(found.emoji, if (found.emoji.details.notoAnimated) EmojiUrl.Type.Lottie else EmojiUrl.Type.SVG))
                ?: return@WithNotoEmoji null
            if (found.emoji.details.notoAnimated) {
                val animation = LottieAnimation.create(bytes)
                InlineTextContent(
                    placeholder = Placeholder(1.em, 1.em / animation.sizeRatio(), PlaceholderVerticalAlign.Center),
                    children = {
                        LottieAnimation(animation, "${found.emoji.details.description} emoji", Modifier.fillMaxSize())
                    }
                )
            } else {
                val svg = SVGImage.create(bytes)
                InlineTextContent(
                    placeholder = Placeholder(1.em, 1.em / svg.sizeRatio(), PlaceholderVerticalAlign.Center),
                    children = {
                        SVGImage(svg, "${found.emoji.details.description} emoji", Modifier.fillMaxSize())
                    }
                )
            }
        }
    )
}

@Composable
public expect fun WithPlatformEmoji(
    text: String,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
)
