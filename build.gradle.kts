plugins {
    kodein.root
}

allprojects {
    group = "org.kodein.emoji"
    version = "1.4.0"
}

allprojects {
    tasks.configureEach {
        if (name == "kotlinStoreYarnLock") enabled = false
    }
}