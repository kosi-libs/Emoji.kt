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

internal fun genEmojiFiles(outputDir: File, annotatedForms: List<AnnotatedForm>): AnnotatedFormTree {
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
                writer.appendLine("        notoAnimated = ${annotatedForm.notoAnimated},")
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
