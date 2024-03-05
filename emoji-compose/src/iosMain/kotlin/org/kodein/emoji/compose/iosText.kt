package org.kodein.emoji.compose

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.AnnotatedString
import org.kodein.emoji.Emoji


internal actual fun AnnotatedString.Builder.appendNotoPlaceholder(emoji: Emoji, inlineContent: MutableMap<String, InlineTextContent>) {
    append(emoji.details.string)
}
