plugins {
    kodein.root
}

allprojects {
    group = "org.kodein.emoji"
    version = "2.3.0"
}

allprojects {
    tasks.configureEach {
        if (name == "kotlinStoreYarnLock") enabled = false
    }
}

val genEmojis = tasks.register<GenEmojis>("genEmojis")
