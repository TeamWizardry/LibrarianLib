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

    depends("fabric-api", ">=${getVersion("platform_fabricApi")}")
    depends("fabricloader", ">=${getVersion("platform_fabricLoader")}")
    depends("minecraft", getVersion("platform_minecraft"))
    depends("fabric-language-kotlin", ">=${getVersion("mods_fabricLanguageKotlin")}")

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
