plugins {
    kodein.library.mppWithAndroid
    alias(libs.plugins.compose)
    alias(kodeinGlobals.plugins.kotlin.plugin.compose)
}

kotlin.kodein {
    jsEnvBrowserOnly()
    allComposeUi()
    macosArm64()

    common.mainDependencies {
        implementation(libs.compose.runtime)
        implementation(libs.compose.foundation)

        api(projects.emojiKt)
    }

    android {
        target.namespace = "org.kodein.emoji.compose"
        sources.mainDependencies {
            implementation(libs.android.svg)
            implementation(libs.android.lottie)
        }
    }

    createSources("skia") {
        dependsOn(common)
        feedsInto(targets.allComposeUi - targets.android + targets.macosArm64)
    }
}

kodeinUpload {
    name = "Emoji.Compose"
    description = "Emoji support for Compose/Multiplatform"
}
