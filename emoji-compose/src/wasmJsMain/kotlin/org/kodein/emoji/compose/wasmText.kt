package org.kodein.emoji.compose

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.em
import org.kodein.emoji.Emoji


internal actual fun AnnotatedString.Builder.appendNotoPlaceholder(emoji: Emoji, inlineContent: MutableMap<String, InlineTextContent>) {
    val inlineContentID = "emoji:placeholder:${emoji}"
    appendInlineContent(inlineContentID)
    inlineContent[inlineContentID] = InlineTextContent(
        placeholder = Placeholder(1.em, 1.em, PlaceholderVerticalAlign.Center),
        children = {}
    )
}
