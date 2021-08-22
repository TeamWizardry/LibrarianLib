@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

plugins {
    `attribute-conventions`
    `minecraft-conventions`
}

val commonConfig = rootProject.the<CommonConfigExtension>()
val liblibModules = commonConfig.modules

val allTestProjects = liblibModules.map { it.project } + listOf(project(":testcore"))
dependencies {
    allTestProjects.forEach {
        runtimeOnly(it.sourceSets.main.get().output)
        runtimeOnly(it.sourceSets.test.get().output)
        runtimeOnly(project(it.path, configuration = "include"))
        runtimeOnly(project(it.path, configuration = "devClasspath"))
        modRuntime(project(it.path, configuration = "devMod"))
    }
    modRuntime("com.terraformersmc:modmenu:2.0.5")
}

loom {
    runConfigs.configureEach {
        vmArg("-Dlibrarianlib.logging.debug=liblib-*")
    }

    log4jConfigs.setFrom(file("log4j.xml"))
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
