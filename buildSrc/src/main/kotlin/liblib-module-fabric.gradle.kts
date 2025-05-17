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

val module = parent!!.extensions.getByType<ModuleExtension>()

configure<KotlinProjectExtension> {
    explicitApi()
}

version = commonConfig.version

architectury {
    fabric()
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

    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${rootProject.property("fabric_kotlin_version")}")

    api(project(path = module.commonPath, configuration = "namedElements"))
    module.dependencies {
        api(project(it.fabricPath, configuration = "namedElements"))
    }

    modApi(project(path = module.path, configuration = "includeFabric"))
    include(project(path = module.path, configuration = "includeFabric"))

    "shadowBundle"(project(path = module.commonPath, configuration = "transformProductionFabric"))
    "shadowBundle"(project(path = module.path, configuration = "shade"))
    "shadowBundle"(project(path = module.path, configuration = "transitiveShade"))
    "shadowSources"(project(path = module.path, configuration = "shade"))
    "shadowSources"(project(path = module.path, configuration = "transitiveShade"))
}

val generateFabricMod = tasks.register<GenerateFabricModJson>("generateFabricMod") {
    outputRoot.set(generatedResourcesDir.map { it.asFile })

    id.set(module.moduleInfo.modid)
    version.set(commonConfig.version)

    name.set(provider { module.modName })
    description.set(provider { module.description })
    icon.set("ll/icon.png")
    iconFile.set(rootDir.resolve("logo/icon.png"))

    depends("fabric-api", project.property("fabric.dependencies.fabricapi") as String)
    depends("fabricloader", project.property("fabric.dependencies.fabricloader") as String)
    depends("minecraft", project.property("fabric.dependencies.minecraft") as String)
    depends("fabric-language-kotlin", project.property("fabric.dependencies.flk") as String)

    module.moduleInfo.dependencies {
        depends(it.modid, commonConfig.version)
    }
    modMenu.badges.add("library")
    modMenu.parent(
        id = "librarianlib",
        name = project.property("liblib.mod_name") as String,
        description = project.property("liblib.mod_description") as String,
        badges = listOf("library")
    )
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(generateFabricMod)
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
    archiveClassifier.set("fabric")
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

    dependsOn(generateFabricMod)
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    includeEmptyDirs = false
    from(shadowSources.map { it.outputs })
}

val remapSourcesJar = tasks.named<RemapSourcesJarTask>("remapSourcesJar") {
    archiveBaseName.set(module.archiveName)
    archiveClassifier.set("fabric-sources")
}

artifacts {
    add("modJar", remapJar)
    add("sourcesJar", remapSourcesJar)
}
