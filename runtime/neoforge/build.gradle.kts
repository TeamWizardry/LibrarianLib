plugins {
    id("liblib-shared-langs")
    id("liblib-shared-loom")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

val mergedTestModServiceLoadersDir = file("$buildDir/generated/testModServiceLoaders")

loom {
    runConfigs.configureEach {
        property("librarianlib.logging.debug", "liblib_*")
    }
    neoForge {
        mods {
            this@mods.remove<Any>(named("main"))
            for (module in commonConfig.modules) {
                create(module.modid) {
                    dependency(
                         dependencies.project(module.commonPath, configuration = "devRuntime"),
                         dependencies.project(module.neoForgePath, configuration = "devRuntime"),
                         dependencies.project(module.path, configuration = "shade"),
                         dependencies.project(module.path, configuration = "transitiveShade"),
                    )
                }
            }
            named("liblib_testcore") {
                for (module in commonConfig.modules) {
                    dependency(dependencies.project(module.testModPath, configuration = "devRuntime"))
                }
                // this has to be last so the merged service loader files override the individual ones
                modFiles.from(mergedTestModServiceLoadersDir)
            }
        }
    }
}

configurations {
}

dependencies {
    "neoForge"(libs.platform.neoforge)

    for (module in commonConfig.modules) {
        modRuntimeOnly(project(module.path, configuration = "includeNeoForge")) { isTransitive = false }
    }

    implementation(libs.mods.kotlinForForge)
    modImplementation(libs.mods.architecturyNeoforge)
}

/**
 * NeoForge places each mod in its own classloader, partially isolated from all the others. This has... consequences.
 * - Observation: The separate classloaders mean service definitions in one mod aren't visible in the others
 * - Problem: testcore relies on cross-mod service loaders to load the test mods
 * - Solution: Put the testmod outputs into the testcore mod's classloader
 * - Problem: The service loader files all override each other, so only one loads
 * - Solution: Put the testmod outputs directly into the classpath as `implementation` dependencies
 * - Problem: Service loaders still aren't accessible, because the classpaths are still siloed away
 * - Solution: Add a `MANIFEST.MF` file with `FMLModType: LIBRARY`, which puts the jar directly on the classpath
 * - Problem: The resources directory (where the manifest and service loader files are) is now on the classpath, but
 *            the classes directory (where the actual service classes are) isn't
 * - Solution: No good solution, give up on that and go back to putting all the testmods into testcore's classloader
 * - Problem: The service loader files still override each other
 * - Solution: Create a task to merge the service files, and include that directory as the final entry in testcore's
 *             classpath
 */
val mergeTestModServiceLoaders = tasks.create("mergeTestModServiceLoaders") {
    for (module in commonConfig.modules) {
        dependsOn("${module.testModPath}:processResources")
    }
    val serviceLoaderFiles = files(commonConfig.modules.map {
        fileTree(project(it.testModPath).layout.buildDirectory.dir("resources/main/META-INF/services"))
    })
    outputs.dir(mergedTestModServiceLoadersDir)
    inputs.files(serviceLoaderFiles)

    doLast {
        val mergedServicesDir = mergedTestModServiceLoadersDir.resolve("META-INF/services")
        mergedServicesDir.deleteRecursively()
        mergedServicesDir.mkdirs()
        val serviceLoaders = mutableMapOf<String, MutableList<String>>()
        for (file in serviceLoaderFiles) {
            val list = serviceLoaders.getOrPut(file.name) { mutableListOf() }
            list.add(file.readText())
        }
        for ((name, parts) in serviceLoaders) {
            mergedServicesDir.resolve(name).writeText(parts.joinToString("\n"))
        }
    }
}

tasks.named("classes") {
    for (module in commonConfig.modules) {
        dependsOn("${module.commonPath}:classes")
        dependsOn("${module.neoForgePath}:classes")
        dependsOn("${module.testModPath}:classes")
    }
}

tasks.named("processResources") {
    dependsOn(mergeTestModServiceLoaders)
    for (module in commonConfig.modules) {
        dependsOn("${module.commonPath}:processResources")
        dependsOn("${module.neoForgePath}:processResources")
        dependsOn("${module.testModPath}:processResources")
    }
}
