import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("liblib-shared-langs")
    id("liblib-shared-loom")
}

version = commonConfig.version

architectury {
    neoForge()
}

configurations {
    create("modJar") {
        canBe(consumed = true, resolved = false)
    }
}

dependencies {
    "neoForge"("net.neoforged:neoforge:${rootProject.property("neoforge_version")}")

    commonConfig.modules.forEach { module ->
        if (module.name != "testcore") {
            include(project(path = module.neoForgePath, configuration = "modJar"))
        }
    }
}

val generateNeoForgeMod = tasks.register<GenerateNeoForgeModsToml>("generateNeoForgeMod") {
    outputRoot.set(generatedResourcesDir.map { it.asFile })

    license.set("LGPL-3.0")

    mod {
        modId.set("librarianlib")
        version.set(commonConfig.version)
        displayName.set(project.property("liblib.mod_name") as String)
        description.set(project.property("liblib.mod_description") as String)
        displayURL.set("https://github.com/TeamWizardry/LibrarianLib")
        logoFile.set(rootDir.resolve("logo/icon.png"))
        logoFileName.set("ll/icon.png")
        // credits.set("")
        // authors.set("")

        dependency(
            "neoforge",
            project.property("neoforge.dependencies.neoforge") as String
        )
        dependency(
            "minecraft",
            project.property("neoforge.dependencies.minecraft") as String
        )
        dependency(
            "kotlinforforge",
            project.property("neoforge.dependencies.kotlinforforge") as String
        )
    }
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(generateNeoForgeMod)
}

val remapJar = tasks.named<RemapJarTask>("remapJar") {
    archiveBaseName.set("librarianlib")
    archiveClassifier.set("neoforge")
}

artifacts {
    add("modJar", remapJar)
}
