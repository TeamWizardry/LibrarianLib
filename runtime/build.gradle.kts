@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

import org.quiltmc.loom.task.RunGameTask

plugins {
    `minecraft-conventions`
}

val commonConfig = rootProject.the<CommonConfigExtension>()
val liblibModules = commonConfig.modules

val allTestProjects = liblibModules.map { it.project } + listOf(project(":testcore"))
dependencies {

    // used by testcore. I can't use `include` because of transitive dependencies.
    runtimeOnly("org.junit.jupiter:junit-jupiter-api:5.6.2")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    runtimeOnly("org.junit.platform:junit-platform-launcher:1.6.2")

    allTestProjects.forEach {
        runtimeOnly(provider { it.sourceSets.main.get().output })
        runtimeOnly(provider { it.sourceSets.test.get().output })
        runtimeOnly(provider { project(it.path, configuration = "include") })
    }
    modRuntime("com.terraformersmc:modmenu:1.16.5")
}

loom {
    runConfigs.configureEach {
        vmArg("-Dlibrarianlib.logging.debug=liblib-*")
    }
}

tasks.named("processResources") {
    allTestProjects.forEach {
        dependsOn("${it.path}:processResources")
        dependsOn("${it.path}:processTestResources")
    }
}
tasks.named("classes") {
    allTestProjects.forEach {
        dependsOn("${it.path}:classes")
        dependsOn("${it.path}:testClasses")
    }
}
