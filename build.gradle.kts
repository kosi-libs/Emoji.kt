plugins {
    kodein.root
}

allprojects {
    group = "org.kodein.emoji"
    version = "2.1.0"
}

allprojects {
    tasks.configureEach {
        if (name == "kotlinStoreYarnLock") enabled = false
    }
}

val genEmojis = tasks.create<GenEmojis>("genEmojis")
