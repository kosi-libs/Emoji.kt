import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    alias(libs.plugins.compose)
    alias(kodeinGlobals.plugins.kotlin.plugin.compose)
}

kotlin {
    jvm()
    jvmToolchain(17)

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeDemo"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(projects.emojiComposeM2)
        }
        getByName("jvmMain").dependencies {
            implementation(compose.desktop.currentOs)
        }
        getByName("androidMain").dependencies {
            implementation(libs.android.activityCompose)
        }
    }
}

android {
    namespace = "org.kodein.emoji.compose.demo"
    setCompileSdkVersion(35)

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose {
    desktop.application.mainClass = "MainDesktopKt"
}
