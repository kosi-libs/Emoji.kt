import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction


@Suppress("LeakingThis")
abstract class GenEmojis : DefaultTask() {

    @get:InputFile
    abstract val unicodeTextFile: RegularFileProperty

    @get:InputFile
    abstract val notoJsonFile: RegularFileProperty

    @get:OutputDirectory
    abstract val genDirectory: DirectoryProperty

    init {
        group = "build"
        unicodeTextFile.convention(project.layout.projectDirectory.file("src/emoji/emoji-test.txt"))
        notoJsonFile.convention(project.layout.projectDirectory.file("src/emoji/emoji_15_0_ordering.json"))
        genDirectory.convention(project.layout.buildDirectory.dir("gen/emoji"))
    }

    // Generates emojis


    @OptIn(ExperimentalStdlibApi::class)
    @TaskAction
    private fun execute() {
        val entries = getEntriesFromFile(unicodeTextFile.get().asFile)
        val forms = entriesToForms(entries)
        val annotatedForms = annotate(forms, notoJsonFile.get().asFile)

        val outputDir = genDirectory.get().asFile
        outputDir.deleteRecursively()
        outputDir.mkdirs()

        val tree = genEmojiFiles(outputDir, annotatedForms)

        genCollections(outputDir, tree)
    }

}