package org.kodein.emoji.compose

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.kodein.emoji.Emoji
import platform.Foundation.*
import platform.posix.memcpy
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@OptIn(ExperimentalForeignApi::class)
internal actual suspend fun platformDownloadBytes(url: String): ByteArray {
    val request = NSURLRequest.requestWithURL(NSURL(string = url))
    return suspendCoroutine { continuation ->
        NSURLConnection.sendAsynchronousRequest(request, NSOperationQueue.mainQueue) { _, data, error ->
            if (data == null) continuation.resumeWithException(IllegalStateException(error?.localizedDescription() ?: "Download failed"))
            else {
                val bytes = ByteArray(data.length.toInt())
                bytes.usePinned {
                    memcpy(it.addressOf(0), data.bytes, data.length)
                }
                continuation.resume(bytes)
            }
        }
    }
}

@Composable
public actual fun WithPlatformEmoji(
    text: CharSequence,
    fixedImageSize: Boolean,
    content: @Composable (AnnotatedString, Map<String, InlineTextContent>) -> Unit
) {
    val annotatedString = remember(text) { AnnotatedString.Builder().append(text).toAnnotatedString() }
    content(annotatedString, emptyMap())
}

@Composable
internal actual fun PlatformEmojiPlaceholder(emoji: Emoji, modifier: Modifier) {
    EmojiFontPlaceholder(emoji, modifier)
}
