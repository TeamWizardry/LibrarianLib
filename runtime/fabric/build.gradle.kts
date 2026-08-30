import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.named

plugins {
    id("liblib-shared-langs")
    id("liblib-shared-loom")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    runConfigs.configureEach {
        property("librarianlib.logging.debug", "liblib_*")
    }
}

configurations {
    create("modJar") {
        description = "jars to be placed in the mods directory (necessary to remap jar-in-jar dependencies)"
    }
}

dependencies {
    commonConfig.modules.forEach {
        runtimeOnly(project(it.commonPath, configuration = "devRuntime"))
        runtimeOnly(project(it.fabricPath, configuration = "devRuntime"))
        runtimeOnly(project(it.testModPath, configuration = "devRuntime"))
        runtimeOnly(project(it.path, configuration = "shade"))
        runtimeOnly(project(it.path, configuration = "transitiveShade"))
        modRuntimeOnly(project(it.path, configuration = "includeFabric")) { isTransitive = false }
    }
    modRuntimeOnly(libs.runtime.mods.modmenu)
    modApi(libs.runtime.mods.sableFabric)

    modImplementation(libs.platform.fabricLoader)
    modImplementation(libs.platform.fabricApi)
    modImplementation(libs.mods.fabricLanguageKotlin)
    modImplementation(libs.mods.architecturyFabric)
}

val downloadModJars = tasks.register("downloadModJars", Sync::class) {
    from(configurations["modJar"])
    into(project.projectDir.resolve("run/mods"))
}

tasks.processResources {
    dependsOn(downloadModJars)
}
