import com.android.build.gradle.internal.lint.LintModelWriterTask
import com.android.build.gradle.internal.tasks.LintModelMetadataTask

plugins {
    kodein.library.mppWithAndroid
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
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

// https://github.com/JetBrains/compose-multiplatform/issues/4739
tasks.withType<LintModelWriterTask> {
    dependsOn("generateResourceAccessorsForAndroidUnitTest")
}
tasks.withType<LintModelMetadataTask> {
    dependsOn("generateResourceAccessorsForAndroidUnitTest")
}

android {
    namespace = "org.kodein.emoji.compose.m3"
}

kodeinUpload {
    name = "Emoji.Compose.M3"
    description = "Emoji support for Compose/Multiplatform with Material 3"
}
