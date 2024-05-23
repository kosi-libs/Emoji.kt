plugins {
    kodein.library.mppWithAndroid
    alias(libs.plugins.compose)
    alias(kodeinGlobals.plugins.kotlin.plugin.compose)
}

kotlin.kodein {
    allComposeUi()

    common.mainDependencies {
        implementation(kotlin.compose.runtime)
        implementation(kotlin.compose.foundation)
        implementation(kotlin.compose.material3)

        api(projects.emojiKt)
        api(projects.emojiCompose)
    }
}

android {
    namespace = "org.kodein.emoji.compose.m3"
}

kodeinUpload {
    name = "Emoji.Compose.M3"
    description = "Emoji support for Compose/Multiplatform with Material 3"
}
