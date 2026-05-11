package org.kodein.emoji.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import org.kodein.emoji.Emoji


@Composable
public actual fun WithPlatformEmoji(
    text: CharSequence,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
) {
    WithNotoImageEmoji(
        text = text,
        content = content
    )
}

@Composable
internal actual fun PlatformEmojiPlaceholder(emoji: Emoji, modifier: Modifier) {
    Box(modifier)
}
