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
    modRuntimeOnly("com.terraformersmc:modmenu:11.0.3")

    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${rootProject.property("fabric_kotlin_version")}")
    modImplementation("dev.architectury:architectury-fabric:${rootProject.property("architectury_api_version")}")
}
