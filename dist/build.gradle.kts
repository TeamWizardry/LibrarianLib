configurations {
    create("releaseJars") {
        canBe(consumed = false, resolved = true)
    }
}

dependencies {
    "releaseJars"(project(":dist:fabric", configuration = "modJar"))
    "releaseJars"(project(":testcore:fabric", configuration = "modJar"))
    "releaseJars"(project(":dist:neoforge", configuration = "modJar"))
    "releaseJars"(project(":testcore:neoforge", configuration = "modJar"))
}

tasks.register<Copy>("build") {
    from(configurations["releaseJars"])
    into(layout.buildDirectory.dir("libs"))
}
