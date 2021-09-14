@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

import java.util.*

// loom expects the decompiler to be in the buildscript classpath. However, it seems that applying the plugin in
// buildSrc scripts means loom isn't properly in the buildscript classpath.
buildscript {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        // update this to match version in buildSrc
        classpath("net.fabricmc:fabric-loom:0.9.46")
    }
}

plugins {
    `attribute-conventions`
    `minecraft-conventions`
}

apply<CommonConfigPlugin>()

allprojects {
    repositories {
        mavenLocal()
        jcenter()
        mavenCentral()
        // OSSRH, just in case a version hasn't synced to central yet
        maven("https://s01.oss.sonatype.org/content/repositories/releases/")

        maven("https://jitpack.io")
        maven("https://www.cursemaven.com") {
            content { includeGroup("curse.maven") }
        }
        maven("https://repo.spongepowered.org/repository/maven-public/")

        maven("https://raw.githubusercontent.com/Devan-Kerman/Devan-Repo/master/") // for ARRP
        maven("https://maven.terraformersmc.com/releases") // for ModMenu
    }
}

commonConfig {
    val snapshotVersion = System.getenv("SNAPSHOT_REF")?.let { ref ->
        if(!ref.startsWith("refs/heads/"))
            throw IllegalStateException("SNAPSHOT_REF `$ref` doesn't start with refs/heads/")
        val branch = ref.removePrefix("refs/heads/")
        branch.replace("[^.\\w-]".toRegex(), "-") + "-SNAPSHOT"
    }
    val mod_version: String by project
    version = snapshotVersion ?: mod_version

    modules {
        subprojects.forEach {
            if(it.name !in setOf("testcore", "runtime", "dist"))
                create(it.name)
        }
    }
}

loom {
    runConfigs.configureEach {
        isIdeConfigGenerated = false
    }
}

tasks.configureEach {
    if(name == "remapAllJars")
        dependsOn(":remapJar")
}

// ---------------------------------------------------------------------------------------------------------------------
//region // Utilities

open class CreateModule: CopyFreemarker() {
    @Option(option = "name", description = "The name of the module in Title Case. e.g. 'Cool Thing'. " +
            "The PascalCase and lowercase names will be inferred from this")
    var moduleName: Property<String> = project.objects.property()

    @Option(option = "desc", description = "The description of the module. e.g. 'Automatic Cool object generation'")
    var moduleDescription: Property<String> = project.objects.property()

    val humanName: String get() = moduleName.get()
    val pascalName: String get() = humanName.replace(" ", "")
    val camelName: String get() = pascalName.substring(0, 1).toLowerCase(Locale.ROOT) + pascalName.substring(1)
    val lowerName: String get() = pascalName.toLowerCase(Locale.ROOT)
}

// use `./gradlew createModule --name=Whatever`
tasks.register<CreateModule>("createModule") {
    template.set(project.file("modules/_template"))
    outputDirectory.set(project.file("modules"))
    model {
        "humanName" %= humanName
        "PascalName" %= pascalName
        "camelName" %= camelName
        "lowername" %= lowerName
        "description" %= moduleDescription.get()
    }
    doFirst {
        val moduleDirectory = outputDirectory.get().resolve(lowerName)
        if(moduleDirectory.exists())
            throw IllegalArgumentException("Target directory `$moduleDirectory` already exists")
    }
    doLast {
        logger.warn("############################################################################")
        logger.warn("# Some manual actions are still required when adding a module!             #")
        logger.warn("# - Set the maven_description in the new module's gradle.properties        #")
        logger.warn("# - Add `includeModule(\"$lowerName\")` to the settings.gradle.kts file    #")
        logger.warn("# - Add `create(\"$lowerName\")` to the root build.gradle.kts commonConfig #")
        logger.warn("# - Add an item describing the module in the root README.md file. e.g.     #")
        logger.warn("- `$lowerName` – ${moduleDescription.get()}")
        logger.warn("############################################################################")
    }
}

tasks.register<ReplaceTextInPlace>("updateReadmeVersions") {

    fun formatBadge(id: String, label: String, message: String, color: String, alt: String): String {
        val cleanLabel = label.replace("_", "__").replace("-", "--").replace(" ", "_")
        val cleanMessage = message.replace("_", "__").replace("-", "--").replace(" ", "_")
        return """<img id="$id" src="https://img.shields.io/badge/$cleanLabel-$cleanMessage-$color" alt="$alt"/>"""
    }

    val minecraft_version: String by project
    val mod_version: String by project

    replaceIn("README.md") {
        add("""<img id="([^"]*-badge)".*?/>""".toRegex()) { _, match ->
            when (match.group(1)) {
                "mod-version-badge" -> formatBadge(
                    id = "mod-version-badge",
                    label = "LibrarianLib",
                    message = mod_version,
                    color = "blue",
                    alt = "LibrarianLib $mod_version"
                )
                "mc-version-badge" -> formatBadge(
                    id = "mc-version-badge",
                    label = "Minecraft",
                    message = minecraft_version,
                    color = "blue",
                    alt = "Minecraft $minecraft_version"
                )
                else -> match.group()
            }
        }
    }
}

tasks.configureEach {
    if(name == "publish")
        dependsOn(":updateReadmeVersions")
}

//endregion // Utilities
// ---------------------------------------------------------------------------------------------------------------------
