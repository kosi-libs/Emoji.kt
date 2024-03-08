package org.kodein.emoji.compose

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.emoji.Emoji
import java.net.URL


internal actual suspend fun platformDownloadBytes(url: String): ByteArray =
    withContext(Dispatchers.IO) {
        URL(url).openStream().use { it.readAllBytes() }
    }

@Composable
public actual fun WithPlatformEmoji(
    text: CharSequence,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
) {
    content(AnnotatedString.Builder().append(text).toAnnotatedString(), emptyMap())
}

@Composable
internal actual fun PlatformEmojiPlaceholder(emoji: Emoji, modifier: Modifier) {
    EmojiFontPlaceholder(emoji, modifier)
}
