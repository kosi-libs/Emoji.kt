

data class Form(
    val entry: Entry,
    var skinToneIndices: List<Int>? = null,
    val doubleSkinToneZWJs: MutableMap<String, DoubleSkinToneZWJ> = HashMap()
) {
    data class DoubleSkinToneZWJ(
        val code: List<Int>,
        var version: List<Int>,
        val skinToneIndices: List<Int>,
    )
}

private fun laterVersion(l: List<Int>, r: List<Int>): List<Int> {
    val (lMaj, lMin) = l
    val (rMaj, rMin) = r
    return when {
        lMaj > rMaj -> l
        lMaj < rMaj -> r
        lMin > rMin -> l
        lMin < rMin -> r
        else -> l
    }
}

private val skinToneComponents = listOf(0x1F3FB, 0x1F3FC, 0x1F3FD, 0x1F3FE, 0x1F3FF)

internal typealias GrouppedForms = List<List<Form>>

// Sorts entries into forms
internal fun entriesToForms(entries: List<Entry>): GrouppedForms {
    val forms = ArrayList<Form>()
    entries.forEach { entry ->
        val skinTonePositions = entry.code.indices.filter { entry.code[it] in skinToneComponents }
        check(skinTonePositions.size in 0..2) { "Invalid skin tone indices" }
        if (skinTonePositions.isNotEmpty()) {
            val originalCode = entry.code.filterIndexed { index, _ -> index !in skinTonePositions }
            val skinToneIndices = skinTonePositions.mapIndexed { n, p -> p - n }
            val originalEntry = entries.firstOrNull { it.code == originalCode }
            if (originalEntry != null) {
                val form = forms.firstOrNull { it.entry == originalEntry } ?: error("No original form for $entry")
                if (form.skinToneIndices == null) {
                    form.skinToneIndices = skinToneIndices
                } else {
                    check(form.skinToneIndices == skinToneIndices) { "Different skin tone indices for $entry" }
                }
            } else {
                check(skinToneIndices.size == 2) { "No original form for ${entry.description}" }
                val description = entry.description.split(":")[0]
                val form =
                    forms.firstOrNull { it.entry.description == description && it.entry.type == entry.type }
                        ?: forms.firstOrNull { it.entry.description == description }
                        ?: error("No original form for $entry")
                val doubleSkinToneZWJ = Form.DoubleSkinToneZWJ(originalCode, entry.version, skinToneIndices)
                if (entry.type !in form.doubleSkinToneZWJs) {
                    form.doubleSkinToneZWJs[entry.type] = doubleSkinToneZWJ
                } else {
                    val existingDoubleSkinToneZWJ = form.doubleSkinToneZWJs[entry.type]!!
                    existingDoubleSkinToneZWJ.version = laterVersion(existingDoubleSkinToneZWJ.version, entry.version)
                    doubleSkinToneZWJ.version = existingDoubleSkinToneZWJ.version
                    check(form.doubleSkinToneZWJs[entry.type] == doubleSkinToneZWJ) { "Different double skin tone ZWJ for $entry than ($doubleSkinToneZWJ != ${form.doubleSkinToneZWJs[entry.type]})" }
                }
            }
        } else {
            forms.add(Form(entry))
        }
    }
    return forms.groupBy { it.entry.description }.values.toList()
}
