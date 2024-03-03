import java.text.Normalizer


private fun String.removeDiacritics() = Normalizer.normalize(this, Normalizer.Form.NFKD)

private fun String.normalize() =
    this
        .replace("’", "")
        .replace("ñ", "n")
        .removeDiacritics()

internal fun String.pascalCase(): String =
    this
        .normalize()
        .split(Regex("[^a-zA-Z0-9]+"))
        .joinToString("") { word -> word.lowercase().replaceFirstChar { it.uppercase() } }

internal fun String.snakeCase(): String =
    this
        .normalize()
        .split(Regex("[^a-zA-Z0-9]+"))
        .joinToString("_") { it.lowercase() }

internal fun String.kebabCase(): String =
    this
        .normalize()
        .split(Regex("[^a-zA-Z0-9]+"))
        .joinToString("-") { it.lowercase() }
