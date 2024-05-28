import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.buffer
import okio.source
import okio.use
import java.io.File


private data class NotoEmoji(
    val base: List<Int>,
    val emoticons: List<String>,
    val shortcodes: List<String>,
    val animated: Boolean
)

private data class NotoGroup(val emoji: List<NotoEmoji>)

data class AnnotatedForm(
    val mainForm: Form,
    val altForms: List<Form>,
    val emoticons: List<String>,
    val aliases: List<String>,
    val hasNotoImage: Boolean,
    val hasNotoAnimation: Boolean
)

@OptIn(ExperimentalStdlibApi::class)
fun annotate(grouppedForms: GrouppedForms, notoJsonFile: File): List<AnnotatedForm> {
    val adapter = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
        .adapter<List<NotoGroup>>()

    val notoEmojis = notoJsonFile
        .source().buffer().use { adapter.fromJson(it) }!!
        .flatMap { it.emoji }
        .distinctBy { it.base }

    val unicodeAliases = grouppedForms.map { it.first().entry.description.kebabCase() }

    val annotatedForms = ArrayList<AnnotatedForm>()

    val mGrouppedForms = grouppedForms.toMutableList()

    notoEmojis.forEach { notoEmoji ->
        val formIndex = mGrouppedForms.indexOfFirst { list -> list.any { it.entry.code == notoEmoji.base } }
        check(formIndex != -1) { "No match found for $notoEmoji (${notoEmoji.base.joinToString(" ") { it.toString(radix = 16) }})" }
        val forms = mGrouppedForms.removeAt(formIndex)
        val mainForm = forms.first { it.entry.code == notoEmoji.base }
        val aliases = notoEmoji.shortcodes
            .map {
                it
                    .removeSurrounding(":")
                    .kebabCase()
            }
            .filter { it != mainForm.entry.description.kebabCase() }
            .filterNot { it in unicodeAliases }
            .distinct()
        annotatedForms += AnnotatedForm(
            mainForm = mainForm,
            altForms = forms - mainForm,
            emoticons = notoEmoji.emoticons,
            aliases = aliases,
            hasNotoImage = true,
            hasNotoAnimation = notoEmoji.animated
        )
    }

    mGrouppedForms.forEach { forms ->
        val mainForm = forms.firstOrNull { it.entry.type == "minimally-qualified" }
            ?: forms.firstOrNull { it.entry.type == "fully-qualified" }
            ?: error("No minimally-qualified nor fully-qualified forms in $forms")

        annotatedForms += AnnotatedForm(
            mainForm = mainForm,
            altForms = forms - mainForm,
            emoticons = emptyList(),
            aliases = emptyList(),
            hasNotoImage = false,
            hasNotoAnimation = false
        )
    }

    check(annotatedForms.size == grouppedForms.size)

    return grouppedForms.map { forms ->
        annotatedForms.first { it.mainForm in forms }
    }
}
