@file:Suppress("PublicApiImplicitType", "UnstableApiUsage", "PropertyName")

import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `java-library`
    `kotlin-conventions`
    `minecraft-conventions`
}

apply<LibLibModulePlugin>()
val liblib: LibLibModuleExtension = the()

dependencies {
    // Dragging in the entirety of MixinGradle just to compile the mixin connector is entirely unnecessary.
    // This jar contains the single interface and function the generated mixin connector uses.
    compileOnly(files("libs/mixin-connector-api.jar"))
}

val mod_version: String by project
tasks.named<ProcessResources>("processResources") {
    filesMatching("**/mods.toml") {
        filter(ReplaceTokens::class, "tokens" to mapOf("version" to mod_version))
    }
}

// ---------------------------------------------------------------------------------------------------------------------
//region // File generation
// ---------------------------------------------------------------------------------------------------------------------

val generatedMain = file("$buildDir/generated/main")

sourceSets {
    main {
        java.srcDir(generatedMain.resolve("java"))
        resources.srcDir(generatedMain.resolve("resources"))
    }
}

val generateMixinConnector = tasks.register<GenerateMixinConnector>("generateMixinConnector") {
    liblib.modules.forEach { module ->
        from(module.project.map { it.sourceSets.main.get() })
    }
    outputRoot.set(generatedMain.resolve("java"))
    mixinName.set("com.teamwizardry.librarianlib.MixinConnector")
}

val generateCoremodsJson = tasks.register<GenerateCoremodsJson>("generateCoremodsJson") {
    liblib.modules.forEach { module ->
        from(module.project.map { it.sourceSets.main.get() })
    }
    outputRoot.set(generatedMain.resolve("resources"))
}

val generateModuleList = tasks.register("generateModuleList") {
    val modules = liblib.modules.map { it.name }
    val outputFile = generatedMain.resolve("resources/META-INF/ll/modules.txt")
    inputs.property("modules", modules)
    outputs.file(outputFile)

    doLast {
        outputFile.parentFile.mkdirs()
        outputFile.writeText(modules.joinToString("\n"))
    }
}

tasks.named("compileJava") {
    dependsOn(generateMixinConnector)
}
tasks.named("processResources") {
    dependsOn(generateCoremodsJson)
    dependsOn(generateModuleList)
}

//endregion // File generation
// ---------------------------------------------------------------------------------------------------------------------
