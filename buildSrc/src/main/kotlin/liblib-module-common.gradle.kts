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
    mixin.defaultRefmapName.set("ll/${module.name}/${module.name}-refmap.json")
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

    id.set("generated-${module.moduleInfo.modid}-common")
    version.set("0.0.0")

    name.set(provider { "${module.moduleInfo.name} - :common assets" })

    modMenu.badges.add("library")
    modMenu.parent(module.moduleInfo.modid)
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(generateFabricMod)
}
