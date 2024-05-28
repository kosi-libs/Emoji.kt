plugins {
    kodein.library.mpp
}

kotlin.kodein {
    all {
        compilations.main {
            compileTaskProvider { dependsOn(":genEmojis") }
        }
    }

    common.main {
        kotlin.srcDirs(provider {
            rootProject.tasks.getByName<GenEmojis>("genEmojis").genEmojiDirectory
        })
    }
}

kodeinUpload {
    name = "Emoji.Kt"
    description = "Emoji support for Kotlin/Multiplatform"
}