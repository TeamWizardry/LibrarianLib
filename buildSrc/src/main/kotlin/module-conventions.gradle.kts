@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import java.net.URI

plugins {
    id("java-library")
    id("maven-publish")
    //id("signing")
    id("kotlin-conventions")
    id("minecraft-conventions")
    id("com.gradleup.shadow")
}

apply<LibLibModulePlugin>()
val module = the<ModuleExtension>()

group = "com.teamwizardry.librarianlib"
version = commonConfig.version

configurations {

    // ----- Providers -----

    create("devRuntime") {
        description = "Dependencies to put on the development runtime classpath"

        canBe(consumed = true, resolved = false)
    }
    create("devMod") {
        description = "Mods to put on the development runtime classpath"

        canBe(consumed = true, resolved = false)
    }
    create("modJar") {
        description = "The mod jar to be bundled into the final release jar"

        canBe(consumed = true, resolved = false)
    }
    create("testModJar") {
        description = "The test mod to be bundled in for testing outside the dev environment"

        canBe(consumed = true, resolved = false)
    }
    create("sourcesJar") {
        description = "The remapped and shadowed sources of the main mod"

        canBe(consumed = true, resolved = false)
    }

    // ----- Consumers -----

    val shade = create("shade") {
        description = "Dependencies to shade into the mod jar."

        canBe(consumed = false, resolved = true)
    }

    val includeApi = create("includeApi") {
        description = "Jar-in-jar 'api' dependencies"

        canBe(consumed = false, resolved = false)
    }

    val includeImplementation = create("includeImplementation") {
        description = "Jar-in-jar 'implementation' dependencies"

        canBe(consumed = false, resolved = false)
    }

    named("api") {
        extendsFrom(includeApi, shade)
    }
    named("include") {
        extendsFrom(includeApi, includeImplementation)
    }
    named("implementation") {
        extendsFrom(includeImplementation)
    }
    named("devRuntime") {
        extendsFrom(shade)
    }
    named("devMod") {
        extendsFrom(include.get())
    }
}

dependencies {
    testImplementation(project(":testcore", configuration = "namedElements"))

    "devRuntime"(sourceSets.main.get().output)
    "devRuntime"(sourceSets.test.get().output)
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

loom {
    mixin.defaultRefmapName.set("ll/${project.name}/${project.name}-refmap.json")
}

val generateFabricMod = tasks.register<GenerateFabricModJson>("generateFabricMod") {
    outputRoot.set(generated.resolve("resources"))

    id.set(module.moduleInfo.modid)
    version.set(commonConfig.version)

    name.set(project.provider { "LibrarianLib: ${module.displayName}" })
    description.set(project.provider { module.description })
    icon.set("ll/icon.png")
    iconFile.set(rootDir.resolve("logo/icon.png"))

    depends("fabric-api", project.property("mod.dependencies.fabricapi") as String)
    depends("fabricloader", project.property("mod.dependencies.fabricloader") as String)
    depends("minecraft", project.property("mod.dependencies.minecraft") as String)
    depends("fabric-language-kotlin", project.property("mod.dependencies.flk") as String)

    module.moduleInfo.dependencies {
        depends(it.modid, commonConfig.version)
    }
    modMenu.badges.add("library")
    modMenu.parent(
        id = "librarianlib",
        name = project.property("mod.modmenu.liblib_name") as String,
        description = project.property("mod.modmenu.liblib_description") as String,
        badges = listOf("library")
    )
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(generateFabricMod)
}

val generateFabricTestMod = tasks.register<GenerateFabricModJson>("generateFabricTestMod") {
    outputRoot.set(generatedTest.resolve("resources"))

    id.set(module.moduleInfo.modid + "-test")
    version.set(commonConfig.version)

    name.set(project.provider { "${module.displayName} Tests" })
    description.set(project.provider { "Tests for ${module.displayName}" })
    icon.set("ll/test_icon.png")
    iconFile.set(rootDir.resolve("logo/test_icon.png"))

    depends("fabric-api", project.property("mod.dependencies.fabricapi") as String)
    depends("fabricloader", project.property("mod.dependencies.fabricloader") as String)
    depends("minecraft", project.property("mod.dependencies.minecraft") as String)
    depends("fabric-language-kotlin", project.property("mod.dependencies.flk") as String)
    depends(module.moduleInfo.modid, commonConfig.version)

    modMenu.badges.add("library")
    modMenu.parent(
        id = "librarianlib-test",
        name = "LibrarianLib Test Mods",
        description = "The test mods for the various librarianlib modules",
        badges = listOf()
    )
}

tasks.named<ProcessResources>("processTestResources") {
    dependsOn(generateFabricTestMod)
}

// ---------------------------------------------------------------------------------------------------------------------
//region // Build configuration

tasks.named<Jar>("jar") {
    enabled = false
}

val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations.getByName("shade"))
    destinationDirectory.set(file("$buildDir/shadow"))
    archiveClassifier.set("shadow")
    includeEmptyDirs = false

    commonConfig.shadowRules {
        relocate(it.from, it.to)
    }
}

val remapJar = tasks.named<RemapJarTask>("remapJar") {
    inputFile.set(shadowJar.map { it.archiveFile.get() })
    dependsOn(shadowJar)
}

val shadowSources = tasks.register<ShadowSources>("shadowSources") {
    relocators.set(shadowJar.get().relocators)

    dependsOn(generateFabricMod)
    from(sourceSets.main.map { it.allSource })
    sourcesFrom(configurations["shade"])
    into("$buildDir/shadowSources")
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    includeEmptyDirs = false
    from(shadowSources.map { it.outputs })
}

val shadowTestJar = tasks.register<ShadowJar>("shadowTestJar") {
    configurations = listOf()
    destinationDirectory.set(file("$buildDir/shadow"))
    archiveClassifier.set("shadow-test")
    includeEmptyDirs = false
    from(sourceSets.test.get().output)
    commonConfig.shadowRules {
        relocate(it.from, it.to)
    }
}

val remapTestJar = tasks.register<RemapJarTask>("remapTestJar") {
    archiveClassifier.set("tests")
    inputFile.set(shadowTestJar.map { it.archiveFile.get() })
    addNestedDependencies = false
    classpath.from(sourceSets.test.get().compileClasspath)
    dependsOn(shadowTestJar)
}

tasks.named("assemble") {
    dependsOn(remapTestJar)
}

artifacts {
    configurations["namedElements"].artifacts.clear()
    add("namedElements", shadowJar)
    add("modJar", remapJar)
    add("testModJar", remapTestJar)
    add("sourcesJar", sourcesJar)
}

//endregion // Build configuration
// ---------------------------------------------------------------------------------------------------------------------

/* region == Publishing == */

if (project.name != "testcore") {
    module.component.addVariantsFromConfiguration(configurations["modJar"]) {
        mapToMavenScope("compile")
    }
    module.component.addVariantsFromConfiguration(configurations["modJar"]) {
        mapToMavenScope("runtime")
    }
    module.component.addVariantsFromConfiguration(configurations["sourcesJar"]) {
    }

    publishing {
        publications {
            register<MavenPublication>("maven") {
                groupId = commonConfig.mavenGroup
                artifactId = module.name
                version = commonConfig.version

                from(module.component)

                pom {
                    name.set(project.property("maven_name") as String)
                    description.set(project.property("maven_description") as String)
                    url.set("https://github.com/TeamWizardry/LibrarianLib")

                    licenses {
                        license {
                            name.set("LGPL-3.0")
                            url.set("https://opensource.org/licenses/LGPL-3.0")
                        }
                    }
                    developers {
                        developer {
                            id.set("thecodewarrior")
                            name.set("Kate Corcoran")
                            email.set("code@thecodewarrior.dev")
                            url.set("https://thecodewarrior.dev")
                        }

                        developer {
                            id.set("librarianlib-contributors")
                            name.set("LibrarianLib Contributors")
                        }
                    }
                    scm {
                        connection.set("scm:git:https://github.com/TeamWizardry/LibrarianLib.git")
                        developerConnection.set("scm:git:ssh://github.com:TeamWizardry/LibrarianLib.git")
                        url.set("https://github.com/TeamWizardry/LibrarianLib")
                    }
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
                            addDependencyNode(commonConfig.mavenGroup, dep.mavenName, commonConfig.version, "compile")
                        }

                        addDependencyNode(
                            "net.fabricmc.fabric-api",
                            "fabric-api",
                            "[${project.property("fabric_version")},)",
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
                            "[${project.property("loader_version")},)",
                            "runtime"
                        )
                    }
                }
            }
        }

        repositories {
            maven {
                name = "ossrh"

                val stagingRepo = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                val snapshotRepo = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                url = URI(if (commonConfig.version.endsWith("SNAPSHOT")) snapshotRepo else stagingRepo)
                credentials {
                    username =
                        project.findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME") ?: "N/A"
                    password =
                        project.findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD") ?: "N/A"
                }
            }
        }
    }

//    signing {
//        if (System.getenv("SIGNING_KEY") != null) {
//            useInMemoryPgpKeys(
//                System.getenv("SIGNING_KEY_ID"),
//                System.getenv("SIGNING_KEY"),
//                System.getenv("SIGNING_KEY_PASSWORD")
//            )
//        } else {
//            useGpgCmd()
//        }
//
//        sign(publishing.publications["maven"])
//    }

    // disable publishing gradle module metadata
    tasks.withType<GenerateModuleMetadata> {
        enabled = false
    }
}

/* endregion == Publishing == */
