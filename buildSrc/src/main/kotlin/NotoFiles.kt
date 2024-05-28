import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


private fun ExecutorService.download(client: OkHttpClient, url: String, file: File) {
    if (file.exists()) return

    submit {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        file.outputStream().use { output ->
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) error(response)
                response.body!!.byteStream().use { input ->
                    input.copyTo(output)
                }
            }
        }
    }
}

internal fun downloadNotoFiles(forms: List<AnnotatedForm>, cacheDir: File) {

    val httpClient = OkHttpClient()
    val exec = Executors.newFixedThreadPool(16)

    forms.forEach { form ->
        val code = form.mainForm.entry.code.joinToString("_") { it.toString(radix = 16) }
        if (form.hasNotoImage) {
            exec.download(httpClient, "https://fonts.gstatic.com/s/e/notoemoji/latest/$code/emoji.svg", cacheDir.resolve("$code.svg"))
        }
        if (form.hasNotoAnimation) {
            exec.download(httpClient, "https://fonts.gstatic.com/s/e/notoemoji/latest/$code/lottie.json", cacheDir.resolve("$code.json"))
        }
    }

    exec.shutdown()
    exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)
}
