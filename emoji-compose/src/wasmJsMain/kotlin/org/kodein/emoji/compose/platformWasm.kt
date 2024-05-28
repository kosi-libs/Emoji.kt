package org.kodein.emoji.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.kodein.emoji.Emoji
import org.w3c.fetch.Response


internal actual suspend fun platformDownloadBytes(url: String): ByteArray {
    val response = window.fetch(url).await<Response>()
    val bufferPromise = response.arrayBuffer()
    val buffer = bufferPromise.await<ArrayBuffer>()
    val array = Int8Array(buffer)
    val ba = ByteArray(array.length) { array[it] }
    return ba
}

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
