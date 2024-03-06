package org.kodein.emoji.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import org.kodein.emoji.Emoji
import org.kodein.emoji.codePoints
import kotlin.jvm.JvmInline


internal expect suspend fun platformDownloadBytes(url: String): ByteArray

@JvmInline
public value class EmojiUrl private constructor(public val url: String) {
    public enum class Type(public val file: String) { SVG("emoji.svg"), Lottie("lottie.json") }
    public companion object {
        public const val notoBaseUrl: String = "https://fonts.gstatic.com/s/e/notoemoji/latest"
        public fun from(emoji: Emoji, type: Type): EmojiUrl {
            val code = emoji.details.codePoints().joinToString("_") { it.toString(radix = 16) }
            return EmojiUrl("$notoBaseUrl/$code/${type.file}")
        }
    }
    public val type: Type get() {
        Type.entries.forEach {
            if (url.endsWith(it.file)) return it
        }
        error("Could not find type of $url")
    }
    public val code: String get() = url.split('/').let { it[it.lastIndex - 1] }
}

public suspend fun simpleDownloadBytes(url: EmojiUrl): ByteArray =
        platformDownloadBytes(url.url)

public val LocalEmojiDownloader: ProvidableCompositionLocal<suspend (EmojiUrl) -> ByteArray> =
    compositionLocalOf { ::simpleDownloadBytes }

@Composable
public fun ProvideEmojiDownloader(
    download: suspend (EmojiUrl) -> ByteArray,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        value = LocalEmojiDownloader provides download,
        content = content
    )
}
