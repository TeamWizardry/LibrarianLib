import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    id("liblib-shared-langs")
    id("liblib-shared-loom")
    id("com.gradleup.shadow")
    id("maven-publish")
}

apply<ModPublishingPlugin>()
val modComponent = the<ModPublishingExtension>().component

val module = parent!!.extensions.getByType<ModuleExtension>()

configure<KotlinProjectExtension> {
    explicitApi()
}

architectury {
    common(commonConfig.platforms)
}

loom {
    mixin.defaultRefmapName.set(module.commonRefmap)
}

configurations {
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
        description = "The common api jar to be published to maven"
        canBe(consumed = true, resolved = false)
    }
    create("sourcesJar") {
        description = "The remapped and shadowed common sources"
        canBe(consumed = true, resolved = false)
    }
}

dependencies {
    "devRuntime"(sourceSets.main.get().output)

    api(project(path = module.path, configuration = "shade"))
    api(project(path = module.path, configuration = "transitiveShade"))
    module.dependencies {
        api(project(it.commonPath, configuration = "namedElements"))
    }

    "shadowBundle"(project(path = module.path, configuration = "shade"))
    "shadowBundle"(project(path = module.path, configuration = "transitiveShade"))
    "shadowSources"(project(path = module.path, configuration = "shade"))
    "shadowSources"(project(path = module.path, configuration = "transitiveShade"))
}

// Classpath entries without a mod json aren't treated like resource packs, so we need to generate a dummy file.
// This file is excluded when making the shadow jar
val generateFabricMod = tasks.register<GenerateFabricModJson>("generateFabricMod") {
    outputRoot.set(generatedResourcesDir.map { it.asFile })

    id.set("generated_${module.moduleInfo.modid}_common")
    version.set("0.0.0")

    name.set(provider { "${module.moduleInfo.name} - :common assets" })

    modMenu.badges.add("library")
    modMenu.parent(module.moduleInfo.modid)
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(generateFabricMod)
}

/**
 * - Problem: NeoForge fails to launch because it uses the refmaps in dev
 * - Solution: Remove the refmap key so mixin can inject it later
 * - Problem: The task that normally injects it is `remapJar`, which doesn't run on the common module
 * - Solution: Use a variable and expand it when making the final jar file
 *
 * Also excludes the dev-env-only fabric.mod.json file generated above so it won't be shadowed into the platform mods
 */
val jar = tasks.named<Jar>("jar") {
    filesMatching("**/*.mixins.json") {
        expand("refmap_name" to module.commonRefmap)
    }
    exclude("fabric.mod.json")
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
    dependsOn(shadowJar)
    inputFile.set(shadowJar.map { it.archiveFile.get() })
}

val shadowSources = tasks.register<ShadowSources>("shadowSources") {
    relocators.set(shadowJar.map { it.relocators }.map { it.get() })

    from(sourceSets.main.map { it.allSource })
    sourcesFrom(project.configurations.getByName("shadowSources"))
    into(layout.buildDirectory.dir("shadowSources"))

    dependsOn(generateFabricMod)
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    destinationDirectory.set(layout.buildDirectory.dir("devlibs"))
    archiveClassifier = "dev-sources"

    includeEmptyDirs = false
    from(shadowSources.map { it.outputs })
}

val remapSourcesJar = tasks.named<RemapSourcesJarTask>("remapSourcesJar") {
    archiveBaseName.set(module.archiveName)
    archiveClassifier.set("sources")
}

artifacts {
    add("modJar", remapJar)
    add("sourcesJar", remapSourcesJar)
}

/* region == Publishing == */

modComponent.addVariantsFromConfiguration(configurations["modJar"]) {
    mapToMavenScope("compile")
}
modComponent.addVariantsFromConfiguration(configurations["modJar"]) {
    mapToMavenScope("runtime")
}
modComponent.addVariantsFromConfiguration(configurations["sourcesJar"]) {
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            groupId = commonConfig.mavenGroup
            artifactId = module.apiMavenName
            version = commonConfig.version

            from(modComponent)

            pom {
                name.set(provider { module.modName })
                description.set(provider { module.description })

                withXml {
                    val depsNode = asNode().appendNode("dependencies")

                    fun addDependencyNode(groupId: String, artifactId: String, version: String, scope: String) {
                        val depNode = depsNode.appendNode("dependency")
                        depNode.appendNode("groupId", groupId)
                        depNode.appendNode("artifactId", artifactId)
                        depNode.appendNode("version", version)
                        depNode.appendNode("scope", scope)
                    }

                    for (dep in module.moduleInfo.dependencies) {
                        addDependencyNode(
                            commonConfig.mavenGroup,
                            dep.apiMavenName,
                            commonConfig.version,
                            "compile"
                        )
                    }
                }
            }
        }
    }
}

// disable publishing gradle module metadata
tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

/* endregion == Publishing == */
