@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

plugins {
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
}

dependencies {
    commonConfig.modules.forEach {
        runtimeOnly(project(it.path, configuration = "devRuntime"))
        modRuntimeOnly(project(it.path, configuration = "devMod"))
    }
    modRuntimeOnly("com.terraformersmc:modmenu:11.0.3")
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
    commonConfig.modules.forEach {
        dependsOn("${it.path}:processResources")
        dependsOn("${it.path}:processTestResources")
    }
}
tasks.named("classes") {
    commonConfig.modules.forEach {
        dependsOn("${it.path}:classes")
        dependsOn("${it.path}:testClasses")
    }
}

//endregion // Runtime environment
// ---------------------------------------------------------------------------------------------------------------------
