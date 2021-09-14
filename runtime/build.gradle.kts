@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

plugins {
    `attribute-conventions`
    `minecraft-conventions`
}

loom {
    runConfigs.configureEach {
        vmArg("-Dlibrarianlib.logging.debug=liblib-*")
        isIdeConfigGenerated = true
    }

    log4jConfigs.setFrom(file("log4j.xml"))
    remapArchives.set(false)
}

val allModules = commonConfig.modules.map { it.path } + listOf(":testcore")

dependencies {
    allModules.forEach {
        runtimeOnly(project(it, configuration = "devRuntime"))
        modRuntime(project(it, configuration = "devMod"))
    }
    modRuntime("com.terraformersmc:modmenu:2.0.5")
}
tasks.named("processResources") {
    allModules.forEach {
        dependsOn("$it:processResources")
        dependsOn("$it:processTestResources")
    }
}
tasks.named("classes") {
    allModules.forEach {
        dependsOn("$it:classes")
        dependsOn("$it:testClasses")
    }
}

//endregion // Runtime environment
// ---------------------------------------------------------------------------------------------------------------------
