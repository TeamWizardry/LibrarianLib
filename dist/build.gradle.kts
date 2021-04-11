@file:Suppress("PublicApiImplicitType", "UnstableApiUsage", "PropertyName")

import net.minecraftforge.gradle.mcp.task.GenerateSRG
import net.minecraftforge.gradle.userdev.tasks.RenameJar
import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.ProjectLocalConfigurations

plugins {
    `maven-publish`
    `java-library`
    `kotlin-conventions`
    `minecraft-conventions`
    `publish-conventions`
}

val commonConfig = rootProject.the<CommonConfigExtension>()
val liblibModules = commonConfig.modules

group = "com.teamwizardry.librarianlib"
version = commonConfig.version

dependencies {
    // Dragging in the entirety of MixinGradle just to compile the mixin connector is entirely unnecessary.
    // This jar contains the single interface and function the generated mixin connector uses.
    compileOnly(files("libs/mixin-connector-api.jar"))
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("**/mods.toml") {
        filter(ReplaceTokens::class, "tokens" to mapOf("version" to commonConfig.version))
    }
}

// ---------------------------------------------------------------------------------------------------------------------
//region // File generation

val generatedMain = file("$buildDir/generated/main")

sourceSets {
    main {
        java.srcDir(generatedMain.resolve("java"))
        resources.srcDir(generatedMain.resolve("resources"))
    }
}

val generateMixinConnector = tasks.register<GenerateMixinConnector>("generateMixinConnector") {
    liblibModules.forEach { module ->
        from(module.project.sourceSets.main.get())
    }
    outputRoot.set(generatedMain.resolve("java"))
    mixinName.set("com.teamwizardry.librarianlib.MixinConnector")
}

val generateCoremodsJson = tasks.register<GenerateCoremodsJson>("generateCoremodsJson") {
    liblibModules.forEach { module ->
        from(module.project.sourceSets.main.get())
    }
    outputRoot.set(generatedMain.resolve("resources"))
}

val generateModuleList = tasks.register("generateModuleList") {
    val modules = liblibModules.map { it.name }
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

// ---------------------------------------------------------------------------------------------------------------------
//region // Build configuration

tasks.named("jar") { enabled = false }
tasks.whenTaskAdded {
    // disable the one automatically created for the `jar` task, since that jar won't exist when it tries to run
    if(name == "reobfJar") {
        enabled = false
    }
}

val deobfJar = tasks.register<Jar>("deobfJar") {
    archiveBaseName.set("librarianlib")
    includeEmptyDirs = false
    liblibModules.forEach { module ->
        // ForgeGradle resolves this immediately anyway, so whatever.
        val moduleJar = module.project.tasks.getByName("deobfJar")
        dependsOn(moduleJar)
        from(zipTree(moduleJar.outputs.files.singleFile))
    }
}

val obfJar = tasks.create<Jar>("obfJar") {
    archiveBaseName.set("librarianlib")
    classifier = "obf"
    dependsOn(deobfJar)
    includeEmptyDirs = false
    from(deobfJar.map { zipTree(it.archiveFile) })
}
reobf.create("obfJar")

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveBaseName.set("librarianlib")
    classifier = "sources"
    includeEmptyDirs = false
    liblibModules.forEach { module ->
        // ForgeGradle resolves this immediately anyway, so whatever.
        val moduleJar = module.project.tasks.getByName("sourcesJar")
        dependsOn(moduleJar)
        from(zipTree(moduleJar.outputs.files.singleFile))
    }
}

tasks.named("assemble") {
    liblibModules.forEach { module ->
        dependsOn(module.project.tasks.named("assemble"))
    }
}

//endregion // Build configuration
// ---------------------------------------------------------------------------------------------------------------------

artifacts {
    add("publishedRuntime", deobfJar)
    add("publishedSources", sourcesJar)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.teamwizardry.librarianlib"
            artifactId = "librarianlib"
            version = commonConfig.version

            from(components["mod"])
        }
    }
}

tasks.create("publishAllToMavenLocal") {
    group = "Publishing"
    dependsOn(tasks.named("publishToMavenLocal"))
    liblibModules.forEach { module ->
        dependsOn(module.project.tasks.getByName("publishToMavenLocal"))
    }
}
