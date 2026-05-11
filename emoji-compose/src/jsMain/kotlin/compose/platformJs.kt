package org.kodein.emoji.compose

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.fetch.Response


@OptIn(ExperimentalWasmJsInterop::class)
internal actual suspend fun platformDownloadBytes(url: String): ByteArray {
    val response = window.fetch(url).await<Response>()
    val bufferPromise = response.arrayBuffer()
    val buffer = bufferPromise.await<ArrayBuffer>()
    val array = Int8Array(buffer)
    val ba = ByteArray(array.length) { array[it] }
    return ba
}
