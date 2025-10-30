plugins {
    kotlin("jvm") version "2.2.0" // https://docs.gradle.org/current/userguide/compatibility.html#kotlin
}

repositories {
    mavenCentral()

    maven("https://css4j.github.io/maven/") {
        mavenContent { releasesOnly() }
        content {
            includeGroup("com.github.css4j")
            includeGroup("io.sf.carte")
            includeGroup("io.sf.jclf")
        }
    }

}

kotlin {
    jvmToolchain(17)
}

//noinspection UseTomlInstead
dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    val moshiVersion = "1.15.2" // https://github.com/square/moshi/tags
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    implementation("com.squareup.okhttp3:okhttp:5.2.1") // https://square.github.io/okhttp/changelogs/changelog

//    implementation("com.github.weisj:jsvg:1.4.0")
}
