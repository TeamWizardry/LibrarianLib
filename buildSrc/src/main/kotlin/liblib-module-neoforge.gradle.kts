import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.DontIncludeResourceTransformer
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    id("liblib-shared-langs")
    id("liblib-shared-loom")
    id("com.gradleup.shadow")
}

if (project.findProperty("loom.platform") != "neoforge") {
    throw IllegalStateException("NeoForge modules must have `loom.platform=neoforge` in their gradle.properties file")
}

val module = parent!!.extensions.getByType<ModuleExtension>()

configure<KotlinProjectExtension> {
    explicitApi()
}

version = commonConfig.version

architectury {
    neoForge()
}

configurations {
    // Files in this configuration will be bundled into your mod using the Shadow plugin.
    // Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
    create("shadowBundle") {
        canBe(consumed = false, resolved = true)
    }
    create("shadowSources") {
        canBe(consumed = false, resolved = true)
    }
    create("devRuntime") {
        canBe(consumed = true, resolved = false)
    }

    create("modJar") {
        description = "The mod jar to be bundled into the final release jar"
        canBe(consumed = true, resolved = false)
    }
    create("sourcesJar") {
        description = "The remapped and shadowed sources of the main mod"
        canBe(consumed = true, resolved = false)
    }
}

dependencies {
    "devRuntime"(sourceSets.main.get().output)

    "neoForge"("net.neoforged:neoforge:${rootProject.property("neoforge_version")}")

    api(project(path = module.commonPath, configuration = "namedElements"))
    module.dependencies {
        api(project(it.neoForgePath, configuration = "namedElements"))
    }

    modApi(project(path = module.path, configuration = "includeNeoForge"))
    include(project(path = module.path, configuration = "includeNeoForge"))

    "shadowBundle"(project(path = module.commonPath, configuration = "transformProductionNeoForge"))
    "shadowBundle"(project(path = module.path, configuration = "shade"))
    "shadowBundle"(project(path = module.path, configuration = "transitiveShade"))
    "shadowSources"(project(path = module.path, configuration = "shade"))
    "shadowSources"(project(path = module.path, configuration = "transitiveShade"))
}

val generateNeoForgeMod = tasks.register<GenerateNeoForgeModsToml>("generateNeoForgeMod") {
    outputRoot.set(generatedResourcesDir.map { it.asFile })

    license.set("LGPL-3.0")

    mod {
        modId.set(module.moduleInfo.modid)
        version.set(commonConfig.version)
        displayName.set(provider { module.modName })
        description.set(provider { module.description })
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

        module.moduleInfo.dependencies {
            dependency(it.modid, commonConfig.version)
        }
    }
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(generateNeoForgeMod)
}

val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations.getByName("shadowBundle"))
    destinationDirectory.set(layout.buildDirectory.dir("devlibs"))
    archiveClassifier = "shadow"

    commonConfig.shadowRules {
        relocate(it.from, it.to)
    }

    mergeServiceFiles()
}

val remapJar = tasks.named<RemapJarTask>("remapJar") {
    archiveBaseName.set(module.archiveName)
    archiveClassifier.set("neoforge")
    dependsOn(shadowJar)
    inputFile.set(shadowJar.map { it.archiveFile.get() })
}

val shadowSources = tasks.register<ShadowSources>("shadowSources") {
    relocators.set(shadowJar.map { it.relocators })

    from(
        sourceSets.main.map { it.allSource },
        project(module.commonPath).sourceSets.main.map { it.allSource }
    )
    sourcesFrom(project.configurations.getByName("shadowSources"))
    into(layout.buildDirectory.dir("shadowSources"))

    dependsOn(generateNeoForgeMod)
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    includeEmptyDirs = false
    from(shadowSources.map { it.outputs })
}

val remapSourcesJar = tasks.named<RemapSourcesJarTask>("remapSourcesJar") {
    archiveBaseName.set(module.archiveName)
    archiveClassifier.set("neoforge-sources")
}

artifacts {
    add("modJar", remapJar)
    add("sourcesJar", remapSourcesJar)
}
