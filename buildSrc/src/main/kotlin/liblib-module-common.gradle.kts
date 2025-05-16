import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    id("liblib-shared-langs")
    id("liblib-shared-loom")
}

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
    create("devRuntime") {
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
