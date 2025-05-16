import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.DontIncludeResourceTransformer
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    id("liblib-shared-langs")
    id("liblib-shared-loom")
    id("com.github.johnrengelman.shadow")
}

if (project.findProperty("loom.platform") != "neoforge") {
    throw IllegalStateException("NeoForge modules must have `loom.platform=neoforge` in their gradle.properties file")
}

val module = parent!!.extensions.getByType<ModuleExtension>()

configure<KotlinProjectExtension> {
    explicitApi()
}

architectury {
    neoForge()
}

loom {
//    mixin.defaultRefmapName.set("ll/${module.name}/${module.name}-neoforge-refmap.json")
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

val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations.getByName("shadowBundle"))
    archiveClassifier = "dev-shadow"

    // The common module needs a `fabric.mod.json` file for assets to load from it. Don't include it in the shadow jar.
    // (classpath entries without a mod json aren't treated like resource packs by fabric)
    transform(DontIncludeResourceTransformer::class.java) { resource = "fabric.mod.json" }

    commonConfig.shadowRules {
        relocate(it.from, it.to)
    }
}

tasks.named<RemapJarTask>("remapJar") {
    dependsOn(shadowJar)
    inputFile.set(shadowJar.map { it.archiveFile.get() })
}

val generateNeoForgeMod = tasks.register<GenerateNeoForgeModsToml>("generateNeoForgeMod") {
    outputRoot.set(generatedResourcesDir.map { it.asFile })

//    We don't use KFF for its mod loader, only the kotlin stdlib
//    modLoader.set("kotlinforforge")
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
