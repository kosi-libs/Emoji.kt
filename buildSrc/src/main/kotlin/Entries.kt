import java.io.File


data class Entry(
    val group: String,
    val subgroup: String,
    val type: String,
    val version: List<Int>,
    val code: List<Int>,
    val description: String
)

private const val groupPrefix = "# group: "
private const val subgroupPrefix = "# subgroup: "

private val lineRegex = Regex("(?<code>[0-9A-F]+( [0-9A-F]+)*) +; (?<type>[a-z-]+) +# .+ E(?<version>[0-9]+\\.[0-9]+) (?<description>.+)")

// Parse emoji-test.txt
internal fun getEntriesFromFile(emojiFile: File): List<Entry> {
    val entries = ArrayList<Entry>()
    var group: String = ""
    var subgroup: String = ""
    emojiFile.inputStream().reader().use { input ->
        input.forEachLine { line ->
            if (line.startsWith(groupPrefix)) {
                group = line.removePrefix(groupPrefix)
                return@forEachLine
            }
            if (line.startsWith(subgroupPrefix)) {
                subgroup = line.removePrefix(subgroupPrefix)
                return@forEachLine
            }
            if (line.startsWith("#") || line.isEmpty()) return@forEachLine

            val match = lineRegex.matchEntire(line) ?: error("Unexpected line $line")

            val code = match.groups["code"]!!.value.split(" ").map { it.toInt(radix = 16) }
            val type = match.groups["type"]!!.value
            val (major, minor) = match.groups["version"]!!.value.split(".").map { it.toInt() }
            val description = match.groups["description"]!!.value

            if (type == "component") return@forEachLine
            if (type !in listOf("fully-qualified", "minimally-qualified", "unqualified")) error("Unexpected type $type")

            entries.add(Entry(group, subgroup, type, listOf(major, minor), code, description))
        }
    }
    return entries
}
