package org.kodein.emoji.compose.m2

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import org.kodein.emoji.compose.NotoImageEmoji
import org.kodein.emoji.compose.NotoAnimatedEmoji
import org.kodein.emoji.compose.WithNotoAnimatedEmoji
import org.kodein.emoji.compose.WithNotoImageEmoji
import org.kodein.emoji.compose.WithPlatformEmoji


/**
 * Displays a `String` containing Emoji characters.
 *
 * - On Wasm: Replaces all emojis with [NotoImageEmoji].
 * - On all other platforms: does not modify the text at all.
 *
 * @see androidx.compose.material.Text
 */
@Composable
public fun TextWithPlatformEmoji(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    WithPlatformEmoji(text) { emojiAnnotatedString, emojiInlineContent ->
        Text(
            text = emojiAnnotatedString,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            inlineContent = emojiInlineContent,
            onTextLayout = onTextLayout,
            style = style
        )
    }
}

/**
 * Displays an `AnnotatedString` containing Emoji characters.
 *
 * - On Wasm: Replaces all emojis with [NotoImageEmoji].
 * - On all other platforms: does not modify the text at all.
 *
 * @see androidx.compose.material.Text
 */
@Composable
public fun TextWithPlatformEmoji(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    WithPlatformEmoji(text) { emojiAnnotatedString, emojiInlineContent ->
        Text(
            text = emojiAnnotatedString,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            inlineContent = inlineContent + emojiInlineContent,
            onTextLayout = onTextLayout,
            style = style
        )
    }
}

/**
 * Displays a `String` containing Emoji characters.
 * Replaces all emojis with [NotoImageEmoji].
 *
 * @see androidx.compose.material.Text
 */
@Composable
public fun TextWithNotoImageEmoji(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    WithNotoImageEmoji(text) { emojiAnnotatedString, emojiInlineContent ->
        Text(
            text = emojiAnnotatedString,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            inlineContent = emojiInlineContent,
            onTextLayout = onTextLayout,
            style = style
        )
    }
}

/**
 * Displays an `AnnotatedString` containing Emoji characters.
 * Replaces all emojis with [NotoImageEmoji].
 *
 * @see androidx.compose.material.Text
 */
@Composable
public fun TextWithNotoImageEmoji(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    WithNotoImageEmoji(text) { emojiAnnotatedString, emojiInlineContent ->
        Text(
            text = emojiAnnotatedString,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            inlineContent = inlineContent + emojiInlineContent,
            onTextLayout = onTextLayout,
            style = style
        )
    }
}

/**
 * Displays a `String` containing Emoji characters.
 * Replaces all emojis with [NotoAnimatedEmoji].
 *
 * @param iterations The number of times that the animations will be played (default is infinite).
 * @param speed Speed at which the animations will be rendered.
 * @see androidx.compose.material.Text
 */
@Composable
public fun TextWithNotoAnimatedEmoji(
    text: String,
    modifier: Modifier = Modifier,
    iterations: Int = Int.MAX_VALUE,
    speed: Float = 1f,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    WithNotoAnimatedEmoji(
        text = text,
        iterations = iterations,
        speed = speed
    ) { emojiAnnotatedString, emojiInlineContent ->
        Text(
            text = emojiAnnotatedString,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            inlineContent = emojiInlineContent,
            onTextLayout = onTextLayout,
            style = style
        )
    }
}

/**
 * Displays an `AnnotatedString` containing Emoji characters.
 * Replaces all emojis with [NotoAnimatedEmoji].
 *
 * @param iterations The number of times that the animations will be played (default is infinite).
 * @param speed Speed at which the animations will be rendered.
 * @see androidx.compose.material.Text
 */
@Composable
public fun TextWithNotoAnimatedEmoji(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    iterations: Int = Int.MAX_VALUE,
    speed: Float = 1f,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    WithNotoAnimatedEmoji(text) { emojiAnnotatedString, emojiInlineContent ->
        Text(
            text = emojiAnnotatedString,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            inlineContent = inlineContent + emojiInlineContent,
            onTextLayout = onTextLayout,
            style = style
        )
    }
}