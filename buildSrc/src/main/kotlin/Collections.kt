import java.io.File


private fun List<Int>.insertZeros(indices: List<Int>): List<Int> {
    val result = ArrayList<Int>()
    var start = 0
    indices.forEach { index ->
        result.addAll(subList(start, index))
        result.add(0)
        start = index
    }
    result.addAll(subList(start, size))
    return result
}

// generate collections
internal fun genCollections(outputDir: File, groups: AnnotatedFormTree) {
    groups.forEach { (groupId, subGroups) ->
        val groupDir = outputDir.resolve(groupId)
        subGroups.forEach { (subgroupId, annotatedForms) ->
            val subgroupPCId = subgroupId.pascalCase()
            val subgroupDir = groupDir.resolve(subgroupId)
            subgroupDir.resolve("_all$subgroupPCId.kt").writer().use { writer ->
                writer.appendLine("package org.kodein.emoji.$groupId.$subgroupId")
                writer.appendLine()
                writer.appendLine("import org.kodein.emoji.Emoji")
                writer.appendLine("import org.kodein.emoji.EmojiFinder")
                writer.appendLine("import org.kodein.emoji.addSt1Variations")
                writer.appendLine("import org.kodein.emoji.addSt2Variations")
                writer.appendLine()
                writer.appendLine()
                writer.appendLine("internal fun EmojiFinder.addAll$subgroupPCId() {")
                annotatedForms.forEach { annotatedForm ->
                    val id = annotatedForm.mainForm.entry.description.asEmojiId()
                    (listOf(annotatedForm.mainForm) + annotatedForm.altForms).forEach { form ->
                        writer.appendLine("    add(intArrayOf(${form.entry.code.joinToString { "0x${it.toString(radix = 16)}" }}), _$id)")
                        if (form.skinToneIndices != null) {
                            val codeWithZeros = form.entry.code.insertZeros(form.skinToneIndices!!)
                            val arrayIndices = form.skinToneIndices!!.mapIndexed { n, i -> i + n }
                            writer.appendLine("    addVariations(intArrayOf(${codeWithZeros.joinToString { "0x${it.toString(radix = 16)}" }}), _$id, ${arrayIndices.joinToString()})")
                        }
                        form.doubleSkinToneZWJs.forEach { (_, zwj) ->
                            val codeWithZeros = zwj.code.insertZeros(zwj.skinToneIndices)
                            val arrayIndices = zwj.skinToneIndices.mapIndexed { n, i -> i + n }
                            writer.appendLine("    addVariations(intArrayOf(${codeWithZeros.joinToString { "0x${it.toString(radix = 16)}" }}), _$id, ${arrayIndices.joinToString()})")
                        }
                    }
                }
                writer.appendLine("}")
                writer.appendLine()
                writer.appendLine("internal fun MutableList<Emoji>.addAll$subgroupPCId() {")
                annotatedForms.forEach { annotatedForm ->
                    val id = annotatedForm.mainForm.entry.description.asEmojiId()
                    writer.appendLine("    add(_$id)")
                }
                writer.appendLine("}")
                writer.appendLine()
                writer.appendLine("""
                    /**
                     * All Emoji of the ${annotatedForms.first().mainForm.entry.group}: ${annotatedForms.first().mainForm.entry.subgroup} subgroup.
                     *
                     * WARNING: This can be quite heavy to construct.
                     * This method should be called in background and its result should be cached.
                    */
                """.trimIndent())
                writer.appendLine("public fun Emoji.Companion.all$subgroupPCId(): List<Emoji> =")
                writer.appendLine("    ArrayList<Emoji>(${annotatedForms.size}).apply { addAll$subgroupPCId() }")
            }
        }
        val groupPCId = groupId.pascalCase()
        groupDir.resolve("_all$groupPCId.kt").writer().use { writer ->
            writer.appendLine("package org.kodein.emoji.$groupId")
            writer.appendLine()
            writer.appendLine("import org.kodein.emoji.Emoji")
            writer.appendLine("import org.kodein.emoji.EmojiFinder")
            subGroups.keys.forEach { subgroupId ->
                writer.appendLine("import org.kodein.emoji.$groupId.$subgroupId.*")
            }
            writer.appendLine()
            writer.appendLine()
            writer.appendLine("internal fun EmojiFinder.addAll$groupPCId() {")
            subGroups.keys.forEach { subgroupId ->
                writer.appendLine("    addAll${subgroupId.pascalCase()}()")
            }
            writer.appendLine("}")
            writer.appendLine()
            writer.appendLine("internal fun MutableList<Emoji>.addAll$groupPCId() {")
            subGroups.keys.forEach { subgroupId ->
                writer.appendLine("    addAll${subgroupId.pascalCase()}()")
            }
            writer.appendLine("}")
            writer.appendLine()
            writer.appendLine("""
                /**
                 * All Emoji of the ${subGroups.values.first().first().mainForm.entry.group} group.
                 *
                 * WARNING: This can be quite heavy to construct.
                 * This method should be called in background and its result should be cached.
                */
            """.trimIndent())
            writer.appendLine("public fun Emoji.Companion.all$groupPCId(): List<Emoji> =")
            writer.appendLine("    ArrayList<Emoji>(${subGroups.values.sumOf { it.size }}).apply { addAll$groupPCId() }")
            writer.appendLine()
            writer.appendLine("internal fun all${groupPCId}Subgroups(): Map<String, () -> List<Emoji>> =")
            writer.appendLine("    mapOf(")
            subGroups.keys.forEach { subgroupId ->
                writer.appendLine("        \"$subgroupId\" to { Emoji.all${subgroupId.pascalCase()}() },")
            }
            writer.appendLine("    )")
        }
    }
    outputDir.resolve("_allEmoji.kt").writer().use { writer ->
        writer.appendLine("package org.kodein.emoji")
        writer.appendLine()
        groups.keys.forEach { groupId ->
            writer.appendLine("import org.kodein.emoji.$groupId.*")
        }
        writer.appendLine()
        writer.appendLine()
        writer.appendLine("internal fun EmojiFinder.addAllEmoji() {")
        groups.keys.forEach { groupId ->
            writer.appendLine("    addAll${groupId.pascalCase()}()")
        }
        writer.appendLine("}")
        writer.appendLine()
        var emojiCount = 0
        var aliasCount = 0
        var emoticonCount = 0
        groups.values.forEach { groups ->
            groups.values.forEach { subgroup ->
                subgroup.forEach { form ->
                    emojiCount += 1
                    aliasCount += 1 + form.aliases.size
                    emoticonCount += form.emoticons.size
                }
            }
        }
        writer.appendLine("internal val emojiCount get() = $emojiCount")
        writer.appendLine("internal val emojiAliasCount get() = $aliasCount")
        writer.appendLine("internal val emojiEmoticonCount get() = $emoticonCount")
        writer.appendLine()
        writer.appendLine("""
            /**
             * All known Emoji.
             *
             * WARNING: This can be quite heavy to construct.
             * This method should be called in background and its result should be cached.
            */
        """.trimIndent())
        writer.appendLine("public fun Emoji.Companion.all(): List<Emoji> =")
        writer.appendLine("    ArrayList<Emoji>(emojiCount).apply {")
        groups.keys.forEach { groupId ->
            writer.appendLine("        addAll${groupId.pascalCase()}()")
        }
        writer.appendLine("    }")
        writer.appendLine()
        writer.appendLine("internal fun allEmojiGroups(): Map<String, Map<String, () -> List<Emoji>>> =")
        writer.appendLine("    mapOf(")
        groups.keys.forEach { groupId ->
            writer.appendLine("        \"$groupId\" to all${groupId.pascalCase()}Subgroups(),")
        }
        writer.appendLine("    )")
    }
}
