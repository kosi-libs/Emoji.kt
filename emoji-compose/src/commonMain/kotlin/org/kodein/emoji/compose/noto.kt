package org.kodein.emoji.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.kodein.emoji.Emoji
import org.kodein.emoji.codePoints
import kotlin.math.min


@Composable
internal fun EmojiFontPlaceholder(emoji: Emoji) {
    var textSize: TextUnit by remember { mutableStateOf(0.sp) }
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    BasicText(
        text = emoji.details.string,
        style = TextStyle(fontSize = textSize, textAlign = TextAlign.Center),
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                with (density) {
                    val result = textMeasurer.measure(emoji.details.string, TextStyle(fontSize = size.height.toSp()))
                    if (result.size.width <= size.width && result.size.height <= size.height) {
                        textSize = size.height.toSp()
                    } else {
                        val wFactor = size.width.toFloat() / result.size.width.toFloat()
                        val hFactor = size.height.toFloat() / result.size.height.toFloat()
                        textSize = (size.height * min(wFactor, hFactor)).toSp()
                    }
                }
            }
    )
}

@Composable
internal expect fun PlatformEmojiPlaceholder(emoji: Emoji)

@Composable
public fun NotoImageEmoji(
    emoji: Emoji,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = { PlatformEmojiPlaceholder(emoji) }
) {
    val download = LocalEmojiDownloader.current
    var svg: SVGImage? by remember { mutableStateOf(null) }
    LaunchedEffect(emoji) {
        val bytes = download(EmojiUrl.from(emoji, EmojiUrl.Type.SVG))
        if (bytes != null) {
            svg = SVGImage.create(bytes)
        }
    }

    if (svg != null) {
        SVGImage(svg!!, "${emoji.details.description} emoji", modifier)
    } else {
        placeholder()
    }
}

@Composable
public fun NotoAnimatedEmoji(
    emoji: Emoji,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = { PlatformEmojiPlaceholder(emoji) }
) {
    if (!emoji.details.notoAnimated) {
        NotoImageEmoji(emoji, modifier, placeholder)
        return
    }
    val download = LocalEmojiDownloader.current
    var animation: LottieAnimation? by remember { mutableStateOf(null) }
    LaunchedEffect(emoji) {
        val bytes = download(EmojiUrl.from(emoji, EmojiUrl.Type.Lottie))
        if (bytes != null) {
            animation = LottieAnimation.create(bytes)
        }
    }

    if (animation != null) {
        LottieAnimation(animation!!, "${emoji.details.description} emoji", modifier)
    } else {
        placeholder()
    }
}
