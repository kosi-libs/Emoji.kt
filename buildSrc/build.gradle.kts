plugins {
    kotlin("jvm") version "2.2.0"
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

//noinspection UseTomlInstead
dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.github.weisj:jsvg:1.4.0")
}
