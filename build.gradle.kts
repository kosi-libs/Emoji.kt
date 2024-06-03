plugins {
    kodein.root
}

allprojects {
    group = "org.kodein.emoji"
    version = "2.0.1"
}

allprojects {
    tasks.configureEach {
        if (name == "kotlinStoreYarnLock") enabled = false
    }
}

val genEmojis = tasks.create<GenEmojis>("genEmojis")
