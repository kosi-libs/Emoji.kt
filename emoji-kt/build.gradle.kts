plugins {
    kodein.library.mpp
}

val genEmojis = tasks.create<GenEmojis>("genEmojis")

kotlin.kodein {
    all {
        compilations.main {
            compileTaskProvider { dependsOn(genEmojis) }
        }
    }

    common.main {
        kotlin.srcDirs(genEmojis.genDirectory)
    }
}

kodeinUpload {
    name = "Emoji.Kt"
    description = "Emoji support for Kotlin/Multiplatform"
}