@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
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
    id("testmod-conventions")
    id("com.github.johnrengelman.shadow")
}

apply<LibLibModulePlugin>()
val module = the<ModuleExtension>()
val commonConfig = rootProject.the<CommonConfigExtension>()

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

    create("devClasspath") {
        description = "Dependencies to put on the development runtime classpath"

        canBe(consumed = true, resolved = false)
    }
    create("devMod") {
        description = "Mods to put on the development runtime classpath"

        canBe(consumed = true, resolved = false)
    }

    // ----- Consumers -----

    val shade = create("shade") {
        description = "Dependencies to shade into the mod jar."

        canBe(consumed = false, resolved = true)
        api.get().extendsFrom(this)
    }

    val liblib = named("liblib") { // `named` because liblib is already created by the module plugin
        description = "Inter-module dependencies"

        canBe(consumed = false, resolved = false)
        api.get().extendsFrom(this)
//        getByName("publishedApi").extendsFrom(this)
    }

}

dependencies {
    testImplementation(project(":testcore"))
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

    depends("fabricloader", project.property("fabricmodjson.depends.fabricloader") as String)
    depends("minecraft", project.property("fabricmodjson.depends.minecraft") as String)
    depends("fabric-language-kotlin", "*")

    module.moduleInfo.dependencies {
        depends(it.modid, commonConfig.version)
    }
    modMenu.badges.add("library")
    modMenu.parent(
        id = project.property("fabricmodjson.modmenu.liblib_id") as String,
        name = project.property("fabricmodjson.modmenu.liblib_name") as String,
        description = project.property("fabricmodjson.modmenu.liblib_description") as String,
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

    depends("fabricloader", project.property("fabricmodjson.depends.fabricloader") as String)
    depends("minecraft", project.property("fabricmodjson.depends.minecraft") as String)
    depends("fabric-language-kotlin", "*")
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

tasks.named("jar") { enabled = false }
tasks.whenTaskAdded {
    // disable the one automatically created for the `jar` task, since that jar won't exist when it tries to run
    if (name == "reobfJar") {
        enabled = false
    }
}

val deobfJar = tasks.register<ShadowJar>("deobfJar") {
    configurations = listOf(project.configurations.getByName("shade"))
    archiveClassifier.set("")
    includeEmptyDirs = false

    from(sourceSets.main.map { it.output })
    dependsOn(tasks.named("classes"))
    dependsOn(tasks.named("processResources"))

    commonConfig.shadowRules {
        relocate(it.from, it.to)
    }
}

val obfJar = tasks.register<Jar>("obfJar") {
    dependsOn(deobfJar)
    archiveClassifier.set("obf")
    from(deobfJar.map { zipTree(it.archiveFile) })
}
//reobf.create("obfJar")

val shadowSources = tasks.register<ShadowSources>("shadowSources") {
    relocators.set(deobfJar.get().relocators)

    from(sourceSets.main.map { it.allSource })
    sourcesFrom(configurations["shade"])
    into("$buildDir/shadowSources")
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    includeEmptyDirs = false
    from(shadowSources.map { it.outputs })
}

//endregion // Build configuration
// ---------------------------------------------------------------------------------------------------------------------

// ---------------------------------------------------------------------------------------------------------------------
//region // Documentation

// the default layout shits itself when you use anything other than a descendent of this project
object ModuleLayout : DokkaMultiModuleFileLayout {
    override fun targetChildOutputDirectory(parent: DokkaMultiModuleTask, child: AbstractDokkaTask): File {
        return parent.outputDirectory.get().resolve("modules/${child.project.name}")
    }
}

val dokkaPartialHtml = tasks.register<DokkaTaskPartial>("dokkaPartialHtml") {
    group = "Documentation"
    description = "Generates partial documentation to be merged by 'dokkaMergedHtml'"
    outputDirectory.set(file("$buildDir/dokka/partial"))
}

val dokkaMergedHtml = tasks.register<DokkaMultiModuleTask>("dokkaMergedHtml") {
    group = "Documentation"
    description = "Merges the partial documentation from this module and all its dependencies"

    dependencies {
        "dokkaMergedHtmlPlugin"("org.jetbrains.dokka:all-modules-page-plugin:${DokkaVersion.version}")
    }

    fileLayout.set(ModuleLayout)
    outputDirectory.set(file("$buildDir/dokka/merged"))

    addChildTask("dokkaPartialHtml")
    module.moduleInfo.allDependencies {
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

//dependencies {
////    "publishedRuntime"(project(":zzz:librarianlib"))
//}
//
//artifacts {
//    add("publishedApi", deobfJar)
//    add("publishedSources", sourcesJar)
//    add("publishedJavadoc", dokkaJar)
//    add("publishedObf", obfJar)
//}
//
//publishing.publications.named<MavenPublication>("maven") {
//    artifactId = module.name
//}

//endregion // Publishing
// ---------------------------------------------------------------------------------------------------------------------
