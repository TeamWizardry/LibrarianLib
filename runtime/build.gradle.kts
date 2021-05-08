@file:Suppress("PublicApiImplicitType")

plugins {
    `minecraft-conventions`
}

val commonConfig = rootProject.the<CommonConfigExtension>()
val liblibModules = commonConfig.modules

val allTestProjects = liblibModules.map { it.project } + listOf(project(":testcore"))
dependencies {
    allTestProjects.forEach {
        runtimeOnly(provider { it.sourceSets.main.get().output })
        runtimeOnly(provider { it.sourceSets.test.get().output })
    }
    modRuntime("com.terraformersmc:modmenu:1.16.5")
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
