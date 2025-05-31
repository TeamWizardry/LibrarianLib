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
    id("maven-publish")
}

if (project.findProperty("loom.platform") != "neoforge") {
    throw IllegalStateException("NeoForge modules must have `loom.platform=neoforge` in their gradle.properties file")
}

apply<ModPublishingPlugin>()
val modComponent = the<ModPublishingExtension>().component

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
    create("devRuntime") {
        canBe(consumed = true, resolved = false)
    }

    create("modJar") {
        description = "The mod jar to be bundled into the final release jar"
        canBe(consumed = true, resolved = false)
    }
    create("pubJar") {
        description = "The jar to be published to maven"
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
    archiveBaseName.set(module.neoForgeArchiveName)
    archiveClassifier = ""

    commonConfig.shadowRules {
        relocate(it.from, it.to)
    }

    mergeServiceFiles()
}

val remapJar = tasks.named<RemapJarTask>("remapJar") {
    archiveBaseName.set(module.neoForgeArchiveName)
    dependsOn(shadowJar)
    inputFile.set(shadowJar.map { it.archiveFile.get() })
}

val pubJar = tasks.register<Jar>("pubJar") {
    destinationDirectory.set(layout.buildDirectory.dir("libs/pub"))
    archiveBaseName.set(module.neoForgeArchiveName)
    dependsOn(remapJar)
    from(remapJar.map { zipTree(it.archiveFile.get()) })
    exclude("**/*-refmap.json")
}

artifacts {
    add("modJar", remapJar)
    // neoforge doesn't like it when the published jar has refmaps, so we publish the unmapped jar. I hate this.
    add("pubJar", pubJar)
}

/* region == Publishing == */

val javaComponent = components["java"] as AdhocComponentWithVariants

modComponent.addVariantsFromConfiguration(configurations["pubJar"]) {
    mapToMavenScope("compile")
}
modComponent.addVariantsFromConfiguration(configurations["pubJar"]) {
    mapToMavenScope("runtime")
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            groupId = commonConfig.mavenGroup
            artifactId = module.neoForgeMavenName
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
                            dep.neoForgeMavenName,
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
