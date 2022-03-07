@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

plugins {
    `attribute-conventions`
    `minecraft-conventions`
}

val mixinDir = buildDir.resolve("agent")

loom {
    runConfigs.configureEach {
        vmArg("-Dlibrarianlib.logging.debug=liblib-*")
        vmArg("-javaagent:${mixinDir.resolve("mixin.jar").absolutePath}")
        vmArg("-Xlog:redefine+class+normalize=info")
        vmArg("-Dmixin.agentLogging=false")
        isIdeConfigGenerated = true
    }

    log4jConfigs.setFrom(file("log4j.xml"))
    remapArchives.set(false)
}

val allModules = commonConfig.modules.map { it.path } + listOf(":testcore")

dependencies {
    allModules.forEach {
        runtimeOnly(project(it, configuration = "devRuntime"))
        modRuntimeOnly(project(it, configuration = "devMod"))
    }
    modRuntimeOnly("com.terraformersmc:modmenu:2.0.5")
}

val copyMixinAgent = tasks.register<Sync>("copyMixinAgent") {
    from(configurations.runtimeClasspath)
    include("sponge-mixin-*")
    into(mixinDir)
    eachFile {
        name = "mixin.jar"
    }
}

tasks.named("processResources") {
    dependsOn(copyMixinAgent)
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
