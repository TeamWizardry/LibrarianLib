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

    modImplementation(libs.platform.fabricLoader)
    modImplementation(libs.platform.fabricApi)
    modImplementation(libs.mods.fabricLanguageKotlin)
    modImplementation(libs.mods.architecturyFabric)
}
