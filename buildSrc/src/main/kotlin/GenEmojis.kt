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
    abstract val genEmojiDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val genNotoDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val cacheDirectory: DirectoryProperty

    init {
        group = "build"
        unicodeTextFile.convention(project.layout.projectDirectory.file("definitions/emoji-test.txt"))
        notoJsonFile.convention(project.layout.projectDirectory.file("definitions/emoji_15_0_ordering.json"))
        genEmojiDirectory.convention(project.layout.buildDirectory.dir("gen/emoji"))
        genNotoDirectory.convention(project.layout.buildDirectory.dir("gen/noto"))
        cacheDirectory.convention(project.layout.buildDirectory.dir("cache/noto"))
    }


    @TaskAction
    private fun execute() {
        val entries = getEntriesFromFile(unicodeTextFile.get().asFile)
        val forms = entriesToForms(entries)
        val annotatedForms = annotate(forms, notoJsonFile.get().asFile)

        val cacheDir = cacheDirectory.get().asFile
        cacheDir.mkdirs()
        downloadNotoFiles(annotatedForms, cacheDir)

        val emojiOutputDir = genEmojiDirectory.get().asFile
        emojiOutputDir.deleteRecursively()
        emojiOutputDir.mkdirs()

        val tree = genEmojiFiles(emojiOutputDir, annotatedForms, cacheDir)
        genCollections(emojiOutputDir, tree)
    }

}