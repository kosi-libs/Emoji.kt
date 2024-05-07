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

/**
 * Centralized Emoji [catalog] and [finder] services.
 */
public class EmojiService private constructor(
    public val catalog: EmojiTemplateCatalog,
    public val finder: EmojiFinder,
) {
    public companion object {
        private lateinit var deferred: Deferred<EmojiService>

        /**
         * Before the catalog is initialized (or accessed, as the first access initializes it), this can be assigned a lambda that will configure the catalog.
         */
        public var catalogBuilder: EmojiTemplateCatalog.Builder.() -> Unit = {}
            set(value) {
                if (::deferred.isInitialized) error("Cannot set catalogBuilder after Service has been initialized or accessed.")
                field = value
            }

        /**
         * Initializes the Emoji services in the background.
         * This function does not block.
         */
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

        /**
         * Get the emoji service as a Composable state.
         * @return null if the emoji service is currently initializing.
         */
        @Composable
        public fun get(): EmojiService? {
            initialize()
            val service: EmojiService? by deferred.consumeAsState(null)
            return service
        }

        /**
         * Awaits for the Emoji service to be initialized.
         */
        public suspend fun await(): EmojiService {
            initialize()
            return deferred.await()
        }
    }
}
