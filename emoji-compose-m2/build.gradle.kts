plugins {
    kodein.library.mppWithAndroid
    alias(libs.plugins.compose)
    alias(kodeinGlobals.plugins.kotlin.plugin.compose)
}

kotlin.kodein {
    allComposeUi()

    common.mainDependencies {
        implementation(libs.compose.runtime)
        implementation(libs.compose.foundation)
        implementation(libs.compose.material2)

        api(projects.emojiKt)
        api(projects.emojiCompose)
    }

    android {
        target.namespace = "org.kodein.emoji.compose.m2"
    }
}

kodeinUpload {
    name = "Emoji.Compose.M2"
    description = "Emoji support for Compose/Multiplatform with Material 2"
}
