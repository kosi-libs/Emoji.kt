plugins {
    kodein.library.mppWithAndroid
    alias(libs.plugins.compose)
    alias(kodeinGlobals.plugins.kotlin.plugin.compose)
}

kotlin.kodein {
    jsEnvBrowserOnly()
    allComposeUi()

    common.mainDependencies {
        implementation(libs.compose.runtime)
        implementation(libs.compose.foundation)

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
