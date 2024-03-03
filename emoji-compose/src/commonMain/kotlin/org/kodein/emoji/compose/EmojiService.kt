package org.kodein.emoji.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import kotlinx.coroutines.*
import org.kodein.emoji.Emoji
import org.kodein.emoji.EmojiFinder
import org.kodein.emoji.EmojiTemplateCatalog
import org.kodein.emoji.all


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
internal fun <T> Deferred<T>.consumeAsState(initialValue: T) =
    produceState(
        initialValue = if (isCompleted) getCompleted() else initialValue,
        producer = { value = await() }
    )

public class EmojiService private constructor(
    public val catalog: EmojiTemplateCatalog,
    public val finder: EmojiFinder,
) {
    public companion object {
        private lateinit var deferred: Deferred<EmojiService>

        public var catalogBuilder: EmojiTemplateCatalog.Builder.() -> Unit = {}
            set(value) {
                if (::deferred.isInitialized) error("Cannot set catalogBuilder after Service has been initialized or accessed.")
                field = value
            }

        public fun initialize() {
            if (!::deferred.isInitialized) {
                @OptIn(DelicateCoroutinesApi::class)
                deferred = GlobalScope.async {
                    val catalog = async(Dispatchers.Default) { EmojiTemplateCatalog(Emoji.all(), catalogBuilder) }
                    val finder = async(Dispatchers.Default) { EmojiFinder() }
                    EmojiService(catalog.await(), finder.await())
                }
            }
        }

        @Composable
        public fun get(): EmojiService? {
            initialize()
            val service: EmojiService? by deferred.consumeAsState(null)
            return service
        }

        public suspend fun await(): EmojiService {
            initialize()
            return deferred.await()
        }
    }
}
