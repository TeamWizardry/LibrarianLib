@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import groovy.util.NodeList
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.jetbrains.dokka.DokkaVersion
import org.jetbrains.dokka.gradle.DokkaMultiModuleFileLayout
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id("java-library")
    id("kotlin-conventions")
    id("minecraft-conventions")
    id("publish-conventions")
    id("com.gradleup.shadow")
    id("org.jetbrains.dokka")
}

apply<LibLibModulePlugin>()
val module = the<ModuleExtension>()

group = "com.teamwizardry.librarianlib"
version = commonConfig.version

sourceSets {
    main {
        resources.srcDir("src/main/datagen")
    }
    test {
        resources.srcDir("src/test/datagen")
    }
}

configurations {

    // ----- Providers -----

    create("devRuntime") {
        description = "Dependencies to put on the development runtime classpath"

        canBe(consumed = true, resolved = false)
    }
    create("devMod") {
        description = "Mods to put on the development runtime classpath"

        canBe(consumed = true, resolved = false)
    }
    create("testMod") {
        description = "The test mod to be bundled in for testing outside the dev environment"

        canBe(consumed = true, resolved = false)
    }

    // ----- Consumers -----

    val shade = create("shade") {
        description = "Dependencies to shade into the mod jar."

        canBe(consumed = false, resolved = true)
    }

    val liblib = named("liblib") { // `named` because liblib is already created by the module plugin
        description = "Inter-module dependencies"

        canBe(consumed = false, resolved = false)
    }

    val includeApi = create("includeApi") {
        description = "Jar-in-jar 'api' dependencies"

        canBe(consumed = false, resolved = false)
    }

    val includeImplementation = create("includeImplementation") {
        description = "Jar-in-jar 'implementation' dependencies"

        canBe(consumed = false, resolved = false)
    }

    named("api") {
        extendsFrom(liblib.get(), includeApi, shade)
    }
    named("publishedApi") {
        extendsFrom(liblib.get(), includeApi)
    }
    named("publishedRuntime") {
        extendsFrom(includeApi, includeImplementation)
    }
    named("include") {
        extendsFrom(includeApi, includeImplementation)
    }
    named("implementation") {
        extendsFrom(includeImplementation)
    }
    named("devRuntime") {
        extendsFrom(shade)
    }
    named("devMod") {
        extendsFrom(include.get())
    }
}

dependencies {
    testImplementation(project(":testcore"))

    "devRuntime"(sourceSets.main.get().output)
    "devRuntime"(sourceSets.test.get().output)
}

val generated: File = file("$buildDir/generated/main")
val generatedTest: File = file("$buildDir/generated/test")

sourceSets {
    main {
        java.srcDir(generated.resolve("java"))
        resources.srcDir(generated.resolve("resources"))
    }
    test {
        java.srcDir(generatedTest.resolve("java"))
        resources.srcDir(generatedTest.resolve("resources"))
    }
}

loom {
    mixin.defaultRefmapName.set("ll/${project.name}/${project.name}-refmap.json")
}

val generateFabricMod = tasks.register<GenerateFabricModJson>("generateFabricMod") {
    outputRoot.set(generated.resolve("resources"))
}

configureFabricModJson {
    id.set(module.moduleInfo.modid)
    version.set(commonConfig.version)

    name.set(project.provider { "LibrarianLib: ${module.displayName}" })
    description.set(project.provider { module.description })
    icon.set("ll/icon.png")
    iconFile.set(rootDir.resolve("logo/icon.png"))

    depends("fabric-api", project.property("mod.dependencies.fabricapi") as String)
    depends("fabricloader", project.property("mod.dependencies.fabricloader") as String)
    depends("minecraft", project.property("mod.dependencies.minecraft") as String)
    depends("fabric-language-kotlin", project.property("mod.dependencies.flk") as String)

    module.moduleInfo.dependencies {
        depends(it.modid, commonConfig.version)
    }
    modMenu.badges.add("library")
    modMenu.parent(
        id = "librarianlib",
        name = project.property("mod.modmenu.liblib_name") as String,
        description = project.property("mod.modmenu.liblib_description") as String,
        badges = listOf("library")
    )
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(generateFabricMod)
}

val generateFabricTestMod = tasks.register<GenerateFabricModJson>("generateFabricTestMod") {
    outputRoot.set(generatedTest.resolve("resources"))

    id.set(module.moduleInfo.modid + "-test")
    version.set(commonConfig.version)

    name.set(project.provider { "${module.displayName} Tests" })
    description.set(project.provider { "Tests for ${module.displayName}" })
    icon.set("ll/test_icon.png")
    iconFile.set(rootDir.resolve("logo/test_icon.png"))

    depends("fabric-api", project.property("mod.dependencies.fabricapi") as String)
    depends("fabricloader", project.property("mod.dependencies.fabricloader") as String)
    depends("minecraft", project.property("mod.dependencies.minecraft") as String)
    depends("fabric-language-kotlin", project.property("mod.dependencies.flk") as String)
    depends(module.moduleInfo.modid, commonConfig.version)

    modMenu.badges.add("library")
    modMenu.parent(
        id = "librarianlib-test",
        name = "LibrarianLib Test Mods",
        description = "The test mods for the various librarianlib modules",
        badges = listOf()
    )
}

tasks.named<ProcessResources>("processTestResources") {
    dependsOn(generateFabricTestMod)
}

// ---------------------------------------------------------------------------------------------------------------------
//region // Build configuration

tasks.named<Jar>("jar") {
    enabled = false
}

val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations.getByName("shade"))
    destinationDirectory.set(file("$buildDir/shadow"))
    archiveClassifier.set("shadow")
    includeEmptyDirs = false

    commonConfig.shadowRules {
        relocate(it.from, it.to)
    }
}

val remapJar = tasks.named<RemapJarTask>("remapJar") {
    inputFile.set(shadowJar.map { it.archiveFile.get() })
}

val shadowSources = tasks.register<ShadowSources>("shadowSources") {
    relocators.set(shadowJar.get().relocators)

    dependsOn(generateFabricMod)
    from(sourceSets.main.map { it.allSource })
    sourcesFrom(configurations["shade"])
    into("$buildDir/shadowSources")
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    includeEmptyDirs = false
    from(shadowSources.map { it.outputs })
}

val shadowTestJar = tasks.register<ShadowJar>("shadowTestJar") {
    configurations = listOf()
    destinationDirectory.set(file("$buildDir/shadow"))
    archiveClassifier.set("shadow-test")
    includeEmptyDirs = false
    from(sourceSets.test.get().output)
    commonConfig.shadowRules {
        relocate(it.from, it.to)
    }
}

val remapTestJar = tasks.register<RemapJarTask>("remapTestJar") {
    archiveClassifier.set("tests")
    inputFile.set(shadowTestJar.map { it.archiveFile.get() })
    addNestedDependencies = false
    dependsOn(shadowTestJar)
}

tasks.named("assemble") {
    dependsOn(remapTestJar)
}

//endregion // Build configuration
// ---------------------------------------------------------------------------------------------------------------------

// ---------------------------------------------------------------------------------------------------------------------
//region // Documentation

// the default layout shits itself when you use anything other than a descendent of this project
object ModuleLayout : DokkaMultiModuleFileLayout {
    override fun targetChildOutputDirectory(parent: DokkaMultiModuleTask, child: AbstractDokkaTask): Provider<Directory> {
        return parent.outputDirectory.map { it.dir("modules/${child.project.name}") }
    }
}

val dokkaPartialHtml = tasks.register<DokkaTaskPartial>("dokkaPartialHtml") {
    group = "Documentation"
    description = "Generates partial documentation to be merged by 'dokkaMergedHtml'"
    outputDirectory.set(file("$buildDir/dokka/partial"))
    dependsOn(tasks.named("compileJava"))
    dependsOn(tasks.named("compileKotlin"))
}

val dokkaMergedHtml = tasks.register<DokkaMultiModuleTask>("dokkaMergedHtml") {
    group = "Documentation"
    description = "Merges the partial documentation from this module and all its dependencies"

    dependencies {
        "dokkaMergedHtmlPlugin"("org.jetbrains.dokka:all-modules-page-plugin:${DokkaVersion.version}")
    }

    fileLayout.set(ModuleLayout)
    outputDirectory.set(file("$buildDir/dokka/merged"))

    dependsOn("dokkaPartialHtml")
    addChildTask("dokkaPartialHtml")
    module.moduleInfo.allDependencies {
        dependsOn("${it.path}:dokkaPartialHtml")
        addChildTask("${it.path}:dokkaPartialHtml")
    }

}

val styledDokkaDir = file("$buildDir/dokka/styled")
val styledDokkaHtml = tasks.register<RestyleDokka>("styledDokkaHtml") {
    group = "Documentation"
    description = "Applies customizations and fixes Dokka's god-awful default styles"

    dokkaTask.set(dokkaMergedHtml)
    outputDir.set(styledDokkaDir)
}

val dokkaJar = tasks.register<Jar>("dokkaJar") {
    group = "Documentation"
    description = "Packages the styled Dokka HTML into a jar"
    archiveClassifier.set("javadoc")

    from(styledDokkaDir)
    dependsOn(styledDokkaHtml)
}

//endregion // Documentation
// ---------------------------------------------------------------------------------------------------------------------

// ---------------------------------------------------------------------------------------------------------------------
//region // Publishing

artifacts {
    add("publishedApi", remapJar) {
        builtBy(remapJar)
    }
    add("publishedRuntime", remapJar) {
        builtBy(remapJar)
    }
    add("publishedSources", sourcesJar) {
        builtBy(sourcesJar)
    }
    add("publishedJavadoc", dokkaJar)
    add("testMod", remapTestJar) {
        builtBy(remapTestJar)
    }
}

publishing.publications.named<MavenPublication>("maven") {
    artifactId = module.name
}

//endregion // Publishing
// ---------------------------------------------------------------------------------------------------------------------
