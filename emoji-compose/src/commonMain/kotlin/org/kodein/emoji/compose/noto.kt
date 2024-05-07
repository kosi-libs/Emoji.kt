package org.kodein.emoji.compose

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
import kotlin.math.min


@Composable
internal fun EmojiFontPlaceholder(emoji: Emoji, modifier: Modifier) {
    var textSize: TextUnit by remember { mutableStateOf(0.sp) }
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    BasicText(
        text = emoji.details.string,
        style = TextStyle(fontSize = textSize, textAlign = TextAlign.Center),
        modifier = modifier
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
internal expect fun PlatformEmojiPlaceholder(emoji: Emoji, modifier: Modifier)

/**
 * Shows an Emoji as an image downloaded from the Noto image library.
 *
 * @param emoji The Emoji to render
 * @param modifier The modifier to be applied to the layout.
 * @param placeholder Composable that will be rendered in place during the download of the image.
 */
@Composable
public fun NotoImageEmoji(
    emoji: Emoji,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = { PlatformEmojiPlaceholder(emoji, modifier) }
) {
    val download = LocalEmojiDownloader.current
    var svg: SVGImage? by remember { mutableStateOf(null) }
    LaunchedEffect(emoji) {
        try {
            val bytes = download(EmojiUrl.from(emoji, EmojiUrl.Type.SVG))
            svg = SVGImage.create(bytes)
        } catch (t: Throwable) {
            println("${t::class.simpleName}: ${t.message}")
        }
    }

    if (svg != null) {
        SVGImage(svg!!, "${emoji.details.description} emoji", modifier)
    } else {
        placeholder()
    }
}

/**
 * Shows an animated Emoji if it is [Emoji.Details.notoAnimated], or defers to [NotoImageEmoji] if it is not.
 *
 * @param emoji The Emoji to render
 * @param modifier The modifier to be applied to the layout.
 * @param iterations The number of times that the animation will be played (default is infinite).
 * @param stopAt Progress that the emoji will stop at during its last iteration.
 * @param speed Speed at which the animation will be rendered.
 * @param placeholder Composable that will be rendered in place during the download of the animation.
 */
@Composable
public fun NotoAnimatedEmoji(
    emoji: Emoji,
    modifier: Modifier = Modifier,
    iterations: Int = Int.MAX_VALUE,
    stopAt: Float = 1f,
    speed: Float = 1f,
    placeholder: @Composable () -> Unit = { PlatformEmojiPlaceholder(emoji, modifier) }
) {
    if (!emoji.details.notoAnimated) {
        NotoImageEmoji(emoji, modifier, placeholder)
        return
    }
    val download = LocalEmojiDownloader.current
    var result: Result<LottieAnimation>? by remember { mutableStateOf(null) }
    LaunchedEffect(emoji) {
        result = runCatching {
            val bytes = download(EmojiUrl.from(emoji, EmojiUrl.Type.Lottie))
            LottieAnimation.create(bytes)
        }.also {
            if (it.isFailure) {
                val t = it.exceptionOrNull()!!
                println("${t::class.simpleName}: ${t.message}")
            }
        }
    }

    if (result != null) {
        if (result!!.isSuccess) {
            LottieAnimation(result!!.getOrThrow(), iterations, stopAt, speed, "${emoji.details.description} emoji", modifier)
        } else {
            NotoImageEmoji(emoji, modifier, placeholder)
        }
    } else {
        placeholder()
    }
}
