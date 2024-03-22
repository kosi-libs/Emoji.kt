plugins {
    kodein.library.mppWithAndroid
    alias(libs.plugins.compose)
}

kotlin.kodein {
    allComposeUi()

    common.mainDependencies {
        implementation(kotlin.compose.runtime)
        implementation(kotlin.compose.foundation)
        implementation(kotlin.compose.material)

        api(projects.emojiKt)
        api(projects.emojiCompose)
    }
}

android {
    namespace = "org.kodein.emoji.compose.m2"
}

kodeinUpload {
    name = "Emoji.Compose.M2"
    description = "Emoji support for Compose/Multiplatform with Material 2"
}
