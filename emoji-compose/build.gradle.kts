plugins {
    kodein.library.mppWithAndroid
    alias(libs.plugins.compose)
}

kotlin.kodein {
    allComposeUi()

    common.mainDependencies {
        implementation(kotlin.compose.runtime)
        implementation(kotlin.compose.foundation)

        api(projects.emojiKt)
    }

    android {
        sources.mainDependencies {
            implementation(libs.android.svg)
            implementation(libs.android.lottie)
        }
    }

    createSources("skia") {
        dependsOn(common)
        feedsInto(targets.allComposeUi - targets.android)
    }
}

android {
    namespace = "org.kodein.emoji.compose"
}

kodeinUpload {
    name = "Emoji.Compose"
    description = "Emoji support for Compose/Multiplatform"
}
