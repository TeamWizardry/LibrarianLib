@file:Suppress("UnstableApiUsage")

plugins {
    `minecraft-conventions`
    `kotlin-conventions`
    `testmod-conventions`
    `java-library`
}

configurations {
    create("devRuntime") {
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

    "devRuntime"("org.junit.jupiter:junit-jupiter-api:5.6.2")
    "devRuntime"("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    "devRuntime"("org.junit.platform:junit-platform-launcher:1.6.2")
    "devMod"("pers.solid:brrp-fabric:1.0.4-1.21.1") {
        isTransitive = false
    }
    modImplementation("pers.solid:brrp-fabric:1.0.4-1.21.1") {
        isTransitive = false
    }

    "devRuntime"(sourceSets.main.get().output)
    "devRuntime"(sourceSets.test.get().output)
}

loom {
    mixin.defaultRefmapName.set("liblib-testcore-refmap.json")
}
