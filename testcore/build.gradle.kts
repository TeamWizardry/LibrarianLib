@file:Suppress("UnstableApiUsage")

plugins {
    `minecraft-conventions`
    `kotlin-conventions`
    `testmod-conventions`
    `java-library`
}

configurations {
    create("devClasspath") {
        description = "Dependencies to put on the development runtime classpath"

        canBe(consumed = true, resolved = false)
    }
    create("devMod") {
        description = "Mods to put on the development runtime classpath"

        canBe(consumed = true, resolved = false)
    }
}

group = "com.teamwizardry.librarianlib"
version = "0.0.0"

dependencies {
    api(project(":core"))
    api("org.junit.jupiter:junit-jupiter-api:5.6.2")
    api("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    api("org.junit.platform:junit-platform-launcher:1.6.2")

    "devClasspath"("org.junit.jupiter:junit-jupiter-api:5.6.2")
    "devClasspath"("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    "devClasspath"("org.junit.platform:junit-platform-launcher:1.6.2")
    "devMod"("net.devtech:arrp:0.3.11")
    modImplementation("net.devtech:arrp:0.3.11")
}

loom {
    mixin.defaultRefmapName.set("liblib-testcore-refmap.json")
}
