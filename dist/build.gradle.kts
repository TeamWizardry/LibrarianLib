@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import com.modrinth.minotaur.TaskModrinthUpload
import com.modrinth.minotaur.request.Dependency.DependencyType
import com.modrinth.minotaur.request.VersionType
//import net.fabricmc.loom.configuration.JarManifestConfiguration
import java.util.jar.Manifest

plugins {
    `java-library`
    id("com.modrinth.minotaur") version "1.2.1"
    id("com.matthewprenger.cursegradle") version "1.4.0"
}

configurations {
    create("include") {
        canBe(consumed = false, resolved = true)
        isTransitive = false
    }
    create("includeTest") {
        canBe(consumed = false, resolved = true)
        isTransitive = false
    }
}

version = commonConfig.version

dependencies {
    commonConfig.modules.forEach {
        if (it.name == "testcore") {
            "includeTest"(project(it.path, configuration = "modJar"))
        } else {
            "include"(project(it.path, configuration = "modJar"))
        }
        "includeTest"(project(it.path, configuration = "testModJar"))
    }
}

val generated: File = file("$buildDir/generated/main")
val generatedTest: File = file("$buildDir/generated/test")

sourceSets {
    main {
        java.srcDir(generated.resolve("java"))
        resources.srcDir(generated.resolve("resources"))
    }
    test {
        java.srcDir(generatedTest.resolve("java"))
        resources.srcDir(generatedTest.resolve("resources"))
    }
}

val generateFabricMod = tasks.register<GenerateFabricModJson>("generateFabricMod") {
    outputRoot.set(generated.resolve("resources"))
}
val generateFabricTestMod = tasks.register<GenerateFabricModJson>("generateFabricTestMod") {
    outputRoot.set(generatedTest.resolve("resources"))
}
tasks.named<ProcessResources>("processResources") {
    dependsOn(generateFabricMod)
}
tasks.named<ProcessResources>("processTestResources") {
    dependsOn(generateFabricTestMod)
}

configureFabricModJson {
    id.set("librarianlib")
    version.set(commonConfig.version)

    name.set(project.property("mod.modmenu.liblib_name") as String)
    description.set(project.property("mod.modmenu.liblib_description") as String)
    icon.set("ll/icon.png")
    iconFile.set(rootDir.resolve("logo/icon.png"))

    depends("fabric-api", project.property("mod.dependencies.fabricapi") as String)
    depends("fabricloader", project.property("mod.dependencies.fabricloader") as String)
    depends("minecraft", project.property("mod.dependencies.minecraft") as String)
    depends("fabric-language-kotlin", project.property("mod.dependencies.flk") as String)

    modMenu.hidden.set(true)

    jars.set(project.provider { configurations["include"].resolve().map { it.name } })
}

configureFabricTestModJson {
    id.set("librarianlib_test")
    version.set(commonConfig.version)

    name.set("LibrarianLib Tests")
    description.set("LibrarianLib's test mods")
    icon.set("ll/icon.png")
    iconFile.set(rootDir.resolve("logo/icon.png"))

    depends("fabric-api", project.property("mod.dependencies.fabricapi") as String)
    depends("fabricloader", project.property("mod.dependencies.fabricloader") as String)
    depends("minecraft", project.property("mod.dependencies.minecraft") as String)
    depends("fabric-language-kotlin", project.property("mod.dependencies.flk") as String)

    modMenu.hidden.set(false)

    jars.set(project.provider { configurations["includeTest"].resolve().map { it.name } })
}

val jar = tasks.named<Jar>("jar") {
    archiveBaseName.set("librarianlib")
    from(sourceSets.main.get().output)
    from(configurations["include"]) {
        into("META-INF/jars")
    }
}

val testJar = tasks.register<Jar>("testJar") {
    archiveBaseName.set("librarianlib_test")
    from(sourceSets.test.get().output)
    from(configurations["includeTest"]) {
        into("META-INF/jars")
    }
}

tasks.named("assemble") {
    dependsOn(testJar)
}

curseforge {
    apiKey = project.findProperty("curseforgeApiToken") as String? ?: System.getenv("CURSEFORGE_API_TOKEN") ?: ""

    project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
        id = "252910"
        changelog = ""
        releaseType = project.property("release.curseforge.type") as String
        addGameVersion(project.property("minecraft_version") as String)
        relations(closureOf<CurseRelation> {
            requiredDependency("fabric-language-kotlin")
        })
        mainArtifact(jar.get(), closureOf<CurseArtifact> {
            displayName = "LibrarianLib ${commonConfig.version}"
        })
    })
    options(closureOf<Options> {
        detectNewerJava = true
        forgeGradleIntegration = false
    })
}

val publishModrinth = tasks.register<TaskModrinthUpload>("publishModrinth") {
    token = project.findProperty("modrinthAuthToken") as String? ?: System.getenv("MODRINTH_AUTH_TOKEN") ?: ""
    projectId = "9uQhkMe5"
    versionNumber = commonConfig.version
    versionType = VersionType.valueOf(project.property("release.modrinth.type") as String)
    uploadFile = jar.get()
    addGameVersion(project.property("minecraft_version") as String)
    addLoader("fabric")
    addDependency(project.property("release.modrinth.flk_version_id") as String, DependencyType.REQUIRED)
    dependsOn(jar)
}

tasks.register("release") {
    dependsOn(":updateReadmeVersions", "curseforge", publishModrinth)
}