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

// https://github.com/JetBrains/compose-multiplatform/issues/4739
tasks.withType<LintModelWriterTask> {
    dependsOn("generateResourceAccessorsForAndroidUnitTest")
}
tasks.withType<LintModelMetadataTask> {
    dependsOn("generateResourceAccessorsForAndroidUnitTest")
}

android {
    namespace = "org.kodein.emoji.compose"
}

kodeinUpload {
    name = "Emoji.Compose"
    description = "Emoji support for Compose/Multiplatform"
}
