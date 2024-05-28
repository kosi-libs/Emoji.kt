import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File


internal fun String.asEmojiId() = this
    .replace("1st", "first")
    .replace("2nd", "second")
    .replace("3rd", "third")
    .replace("#", "hash")
    .replace("*", "star")
    .pascalCase()

private fun skinToneIntIndices2CharIndices(code: List<Int>, intIndices: List<Int>): List<Int> =
    intIndices.map { index ->
        code.subList(0, index).sumOf { (if (Character.isBmpCodePoint(it)) 1 else 2).toInt() }
    }

internal typealias AnnotatedFormTree = Map<String, Map<String, List<AnnotatedForm>>>

internal fun genEmojiFiles(outputDir: File, annotatedForms: List<AnnotatedForm>, cacheDir: File): AnnotatedFormTree {
    val ids = LinkedHashMap<String, LinkedHashMap<String, ArrayList<AnnotatedForm>>>()
    annotatedForms
        .forEach { annotatedForm ->
            val groupId = annotatedForm.mainForm.entry.group.snakeCase()
            val subgroupId = annotatedForm.mainForm.entry.subgroup.snakeCase()
            val dir = outputDir.resolve(groupId).resolve(subgroupId)
            dir.mkdirs()
            val id = annotatedForm.mainForm.entry.description.asEmojiId()
            ids.getOrPut(groupId) { LinkedHashMap() }.getOrPut(subgroupId) { ArrayList() }.add(annotatedForm)
            val doubleSkinToneZWJ = annotatedForm.mainForm.doubleSkinToneZWJs["minimally-qualified"] ?: annotatedForm.mainForm.doubleSkinToneZWJs["fully-qualified"]
            val unqualifiedForm = annotatedForm.altForms.firstOrNull { it.entry.type == "unqualified" }

            val notoCode = annotatedForm.mainForm.entry.code.joinToString("_") { it.toString(radix = 16) }

            var notoImageRatio = 0f
            if (annotatedForm.hasNotoImage) {
                val file = cacheDir.resolve("$notoCode.svg")
                val bytes = file.readBytes()
                if (bytes.isNotEmpty()) {
                    val xml = bytes.toString(Charsets.UTF_8)
                    val svg = Regex("<svg\\s[^>]+>").find(xml)
                        ?: error("${file.absolutePath}: Could not find svg markup")
                    val w = Regex("width=\"(?<w>\\d+(?:\\.\\d+)?)(?:px)?\"").find(svg.value)?.groups?.get("w")?.value?.toFloat()
                    val h = Regex("height=\"(?<h>\\d+(?:\\.\\d+)?)(?:px)?\"").find(svg.value)?.groups?.get("h")?.value?.toFloat()
                    if (w != null && h != null) {
                        notoImageRatio = w / h
                    } else {
                        val viewBox = Regex("viewBox=\"-?\\d+(?:\\.\\d+)? -?\\d+(?:\\.\\d+)? (?<w>-?\\d+(?:\\.\\d+)?) (?<h>-?\\d+(?:\\.\\d+)?)\"").find(svg.value)
                        if (viewBox != null) {
                            val vbW = viewBox.groups["w"]!!.value.toFloat()
                            val vbH = viewBox.groups["h"]!!.value.toFloat()
                            notoImageRatio = vbW / vbH
                        } else {
                            error("${file.absolutePath}: Could not find neither viewBox nor width & height.")
                        }
                    }
                }
            }

            var notoAnimationRatio = 0f
            if (annotatedForm.hasNotoAnimation) {
                val file = cacheDir.resolve("$notoCode.json")
                val json = file.readText()
                if (json.isNotEmpty()) {
                    @OptIn(ExperimentalStdlibApi::class)
                    val adapter = Moshi.Builder()
                        .addLast(KotlinJsonAdapterFactory())
                        .build()
                        .adapter<Map<String, Any>>()
                    val map = adapter.fromJson(json) ?: error("${file.absolutePath}: Could not parse JSON.")
                    val w = (map["w"] as? Number)?.toFloat() ?: error("${file.absolutePath}: Could not find w.")
                    val h = (map["h"] as? Number)?.toFloat() ?: error("${file.absolutePath}: Could not find h.")
                    notoAnimationRatio = w / h
                }
            }

            val (itf, impl) = when {
                annotatedForm.mainForm.skinToneIndices == null && doubleSkinToneZWJ == null && unqualifiedForm?.skinToneIndices == null -> "Emoji" to "EmojiImpl"
                annotatedForm.mainForm.skinToneIndices == null && unqualifiedForm?.skinToneIndices?.size == 1 -> "SkinTone1Emoji" to "UnqualifiedSkinTone1EmojiImpl"
                annotatedForm.mainForm.skinToneIndices?.size == 1 && doubleSkinToneZWJ == null -> "SkinTone1Emoji" to "SkinTone1EmojiImpl"
                annotatedForm.mainForm.skinToneIndices?.size == 2 && doubleSkinToneZWJ == null -> "SkinTone2Emoji" to "SkinTone2EmojiImpl"
                annotatedForm.mainForm.skinToneIndices?.size == 1 && doubleSkinToneZWJ != null -> "SkinTone2Emoji" to "SkinTone2EmojiZWJImpl"
                else -> error("Invalid skinTone configuration for ${annotatedForm.mainForm}")
            }
            dir.resolve("$id.kt").outputStream().writer().use { writer ->
                writer.appendLine("package org.kodein.emoji.$groupId.$subgroupId")
                writer.appendLine()
                if (itf != "Emoji") {
                    writer.appendLine("import org.kodein.emoji.Emoji")
                }
                writer.appendLine("import org.kodein.emoji.$itf")
                writer.appendLine("import org.kodein.emoji.$impl")
                writer.appendLine("import org.kodein.emoji.UnicodeVersion")
                writer.appendLine("import org.kodein.emoji.EmojiFinder")
                if (annotatedForm.mainForm.skinToneIndices != null || annotatedForm.mainForm.doubleSkinToneZWJs.isNotEmpty()) {
                    writer.appendLine("import org.kodein.emoji.SkinTone")
                }
                writer.appendLine()
                writer.appendLine()
                val emoticons = annotatedForm.emoticons.map {
                    it
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\$", "\\\$")
                }
                writer.appendLine("internal val _$id: $itf = $impl(")
                writer.appendLine("    details = Emoji.Details(")
                writer.appendLine("        string = \"${annotatedForm.mainForm.entry.code.joinToString("") { Character.toString(it) }}\",")
                writer.appendLine("        description = \"${annotatedForm.mainForm.entry.description}\",")
                writer.appendLine("        unicodeVersion = UnicodeVersion(${annotatedForm.mainForm.entry.version[0]}, ${annotatedForm.mainForm.entry.version[1]}),")
                writer.appendLine("        aliases = listOf(\"${annotatedForm.mainForm.entry.description.kebabCase()}\", ${annotatedForm.aliases.joinToString { "\"$it\"" }}),")
                writer.appendLine("        emoticons = listOf(${emoticons.joinToString { "\"$it\"" }}),")
                writer.appendLine("        notoImageRatio = ${notoImageRatio}f,")
                writer.appendLine("        notoAnimationRatio = ${notoAnimationRatio}f,")
                writer.appendLine("    ),")
                when (impl) {
                    "UnqualifiedSkinTone1EmojiImpl" -> {
                        writer.appendLine("    uqString = \"${unqualifiedForm!!.entry.code.joinToString("") { Character.toString(it) }}\",")
                        val skinToneCharIndices = skinToneIntIndices2CharIndices(unqualifiedForm.entry.code, unqualifiedForm.skinToneIndices!!)
                        writer.appendLine("    sk1c = ${skinToneCharIndices[0]},")
                    }
                    "SkinTone1EmojiImpl" -> {
                        val skinToneCharIndices = skinToneIntIndices2CharIndices(annotatedForm.mainForm.entry.code, annotatedForm.mainForm.skinToneIndices!!)
                        writer.appendLine("    sk1c = ${skinToneCharIndices[0]},")
                    }
                    "SkinTone2EmojiImpl" -> {
                        val skinToneCharIndices = skinToneIntIndices2CharIndices(annotatedForm.mainForm.entry.code, annotatedForm.mainForm.skinToneIndices!!)
                        writer.appendLine("    sk21c = ${skinToneCharIndices[0]},")
                        writer.appendLine("    sk22c = ${skinToneCharIndices[1]},")
                    }
                    "SkinTone2EmojiZWJImpl" -> {
                        val skinToneCharIndices = skinToneIntIndices2CharIndices(annotatedForm.mainForm.entry.code, annotatedForm.mainForm.skinToneIndices!!)
                        writer.appendLine("    sk1c = ${skinToneCharIndices[0]},")
                        writer.appendLine("    zwjTemplate = \"${doubleSkinToneZWJ!!.code.joinToString("") { Character.toString(it) }}\",")
                        writer.appendLine("    zwjUnicodeVersion = UnicodeVersion(${doubleSkinToneZWJ.version[0]}, ${doubleSkinToneZWJ.version[1]}),")
                        val zwjSkinToneCharIndices = skinToneIntIndices2CharIndices(doubleSkinToneZWJ.code, doubleSkinToneZWJ.skinToneIndices)
                        writer.appendLine("    sk21c = ${zwjSkinToneCharIndices[0]},")
                        writer.appendLine("    sk22c = ${zwjSkinToneCharIndices[1]},")
                    }
                }
                writer.appendLine(")")
                writer.appendLine()
                writer.appendLine("""
                    /**
                     * Emoji ${annotatedForm.mainForm.entry.group}: ${annotatedForm.mainForm.entry.subgroup}: ${annotatedForm.mainForm.entry.description}.
                     *
                     * Preferred type is: ${annotatedForm.mainForm.entry.type}.
                    */
                """.trimIndent())
                writer.appendLine("public val Emoji.Companion.$id: $itf get() = _$id")
                annotatedForm.aliases
                    .filter { it.first().isLetter() }
                    .forEach {
                        writer.appendLine()
                        writer.appendLine("""
                            /**
                             * Alias to emoji [$id] (${annotatedForm.mainForm.entry.group}: ${annotatedForm.mainForm.entry.subgroup}: ${annotatedForm.mainForm.entry.description}).
                            */
                        """.trimIndent())
                        writer.appendLine("public val Emoji.Companion.${it.asEmojiId()}: $itf get() = _$id")
                    }
            }
        }
    return ids
}
