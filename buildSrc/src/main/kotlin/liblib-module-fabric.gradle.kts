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

apply<ModPublishingPlugin>()
val modComponent = the<ModPublishingExtension>().component

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
    create("devRuntime") {
        canBe(consumed = true, resolved = false)
    }

    create("modJar") {
        description = "The mod jar to be bundled into the final release jar"
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
}

val generateFabricMod = tasks.register<GenerateFabricModJson>("generateFabricMod") {
    outputRoot.set(generatedResourcesDir.map { it.asFile })

    id.set(module.moduleInfo.modid)
    version.set(commonConfig.version)

    name.set(provider { module.modName })
    description.set(provider { module.description })
    icon.set("ll/icon.png")
    iconFile.set(rootDir.resolve("logo/icon.png"))

    depends("fabric-api", ">=${project.property("fabric_kotlin_version")}")
    depends("fabricloader", ">=${project.property("fabric_loader_version")}")
    depends("minecraft", project.property("minecraft_version") as String)
    depends("fabric-language-kotlin", ">=${project.property("fabric_kotlin_version")}")

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
    archiveBaseName.set(module.fabricArchiveName)
    dependsOn(shadowJar)
    inputFile.set(shadowJar.map { it.archiveFile.get() })
}

artifacts {
    add("modJar", remapJar)
}

/* region == Publishing == */

val javaComponent = components["java"] as AdhocComponentWithVariants

modComponent.addVariantsFromConfiguration(configurations["modJar"]) {
    mapToMavenScope("compile")
}
modComponent.addVariantsFromConfiguration(configurations["modJar"]) {
    mapToMavenScope("runtime")
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            groupId = commonConfig.mavenGroup
            artifactId = module.fabricMavenName
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
                            dep.fabricMavenName,
                            commonConfig.version,
                            "compile"
                        )
                    }

                    addDependencyNode(
                        "net.fabricmc.fabric-api",
                        "fabric-api",
                        "[${project.property("fabric_api_version")},)",
                        "compile"
                    )
                    addDependencyNode(
                        "net.fabricmc",
                        "fabric-language-kotlin",
                        "[${project.property("fabric_kotlin_version")},)",
                        "compile"
                    )
                    addDependencyNode(
                        "net.fabricmc",
                        "fabric-loader",
                        "[${project.property("fabric_loader_version")},)",
                        "runtime"
                    )
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
