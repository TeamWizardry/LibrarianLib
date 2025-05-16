import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.getByType

plugins {
    id("liblib-shared-langs")
    id("liblib-shared-loom")
}

val module = parent!!.extensions.getByType<ModuleExtension>()

architectury {
    common(commonConfig.platforms)
}

configurations {
    create("devRuntime") {
        canBe(consumed = true, resolved = false)
    }
}

dependencies {
    "devRuntime"(sourceSets.main.get().output)

    // Architectury API. This is optional, and you can comment it out if you don't need it.
    modImplementation("dev.architectury:architectury:${rootProject.property("architectury_api_version")}")

    implementation(project(path = module.commonPath, configuration = "namedElements"))
    implementation(project(path = ":testcore:common", configuration = "namedElements"))
}

// Classpath entries without a mod json aren't treated like resource packs, so we need to generate a dummy file.
// This file is excluded when making the shadow jar
val generateFabricMod = tasks.register<GenerateFabricModJson>("generateFabricMod") {
    outputRoot.set(generatedResourcesDir.map { it.asFile })

    id.set("generated_${module.moduleInfo.modid}_testmod")
    version.set("0.0.0")

    name.set(provider { "${module.moduleInfo.name} - :testmod assets" })

    modMenu.badges.add("library")
    modMenu.parent(module.moduleInfo.modid)
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(generateFabricMod)
}
