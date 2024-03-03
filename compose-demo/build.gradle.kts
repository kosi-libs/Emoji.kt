import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    alias(libs.plugins.compose)
}

kotlin {
    jvm()

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
            implementation(projects.emojiCompose)
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
    setCompileSdkVersion(34)

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose {
    desktop.application.mainClass = "MainKt"
    experimental.web.application {}
}
