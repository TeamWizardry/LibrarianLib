import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("liblib-shared-langs")
    id("liblib-shared-loom")
}

version = commonConfig.version

architectury {
    fabric()
}

configurations {
    create("modJar") {
        canBe(consumed = true, resolved = false)
    }
}

dependencies {
    commonConfig.modules.forEach { module ->
        if (module.name != "testcore") {
            include(project(path = module.fabricPath, configuration = "modJar"))
        }
    }
}

val generateFabricMod = tasks.register<GenerateFabricModJson>("generateFabricMod") {
    outputRoot.set(generatedResourcesDir.map { it.asFile })

    id.set("librarianlib")
    version.set(commonConfig.version)

    name.set(project.property("liblib.mod_name") as String)
    description.set(project.property("liblib.mod_description") as String)
    icon.set("ll/icon.png")
    iconFile.set(rootDir.resolve("logo/icon.png"))

    depends("fabric-api", ">=${project.property("fabric_kotlin_version")}")
    depends("fabricloader", ">=${project.property("fabric_loader_version")}")
    depends("minecraft", project.property("minecraft_version") as String)
    depends("fabric-language-kotlin", ">=${project.property("fabric_kotlin_version")}")

    modMenu.badges.add("library")
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(generateFabricMod)
}

val remapJar = tasks.named<RemapJarTask>("remapJar") {
    archiveBaseName.set("librarianlib")
    archiveClassifier.set("fabric")
}

artifacts {
    add("modJar", remapJar)
}
