@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.dokka.DokkaVersion
import org.jetbrains.dokka.gradle.DokkaMultiModuleFileLayout
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.quiltmc.loom.LoomGradleExtension
import org.quiltmc.loom.configuration.ide.RunConfigSettings
import org.quiltmc.loom.task.RunGameTask

plugins {
    id("java-library")
    id("kotlin-conventions")
    id("minecraft-conventions")
    id("publish-conventions")
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

dependencies.attributesSchema {
    attribute(LibLibAttributes.Target.attribute) {
        compatibilityRules.add(LibLibAttributes.Rules.optional())
    }
}

configurations {

    // ----- Providers -----

    apiElements {
        attributes.attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.internal)
    }

    val devClasspath = create("devClasspath") {
        description = "Libraries to put on the development environment classpath"
        canBe(consumed = true, resolved = false)
        attributes.attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.devClasspath)
    }

    // files to copy into the mods directory
    val clientMod = create("clientMod") {
        description = "Mod jars to copy into the client mods/ directory. (not transitive!)"

        canBe(consumed = true, resolved = false)
        attributes.attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.clientMods)
        isTransitive = false
    }
    val serverMod = create("serverMod") {
        description = "Mod jars to copy into the server mods/ directory. (not transitive!)"

        canBe(consumed = true, resolved = false)
        attributes.attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.serverMods)
        isTransitive = false
    }
    val dataMod = create("dataMod") {
        description = "Mod jars to copy into the data mods/ directory. (not transitive!)"

        canBe(consumed = true, resolved = false)
        attributes.attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.dataMods)
        isTransitive = false
    }

    val mod = create("mod") {
        description = "Mod jars to copy into the client, server, and data mods/ directory. (not transitive!)"

        canBe(consumed = false, resolved = false)
        isTransitive = false
        clientMod.extendsFrom(this)
        serverMod.extendsFrom(this)
        dataMod.extendsFrom(this)
    }

    // ----- Consumers -----

    val shade = create("shade") {
        description = "Dependencies to shade into the mod jar."

        canBe(consumed = false, resolved = true)
        devClasspath.extendsFrom(this)
        api.get().extendsFrom(this)
    }

    listOf(compileClasspath, runtimeClasspath, testCompileClasspath, testRuntimeClasspath).forEach {
        it {
            attributes.attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.internal)
        }
    }

    val liblib = named("liblib") { // `named` because liblib is already created by the module plugin
        description = "Inter-module dependencies"

        canBe(consumed = false, resolved = false)
        api.get().extendsFrom(this)
        getByName("publishedApi").extendsFrom(this)
    }

}

//val validateMixinApplication = tasks.register<ValidateMixinApplication>("validateMixinApplication") {
//    from(sourceSets.main)
//    from(sourceSets.test)
//}
//
//tasks.named("compileJava") {
//    dependsOn(validateMixinApplication)
//}
//tasks.named("compileTestJava") {
//    dependsOn(validateMixinApplication)
//}

configure<LoomGradleExtension> {
    runConfigs {
        create("testClient") {
            client()
            source(sourceSets["test"])
        }
        create("testServer") {
            server()
            source(sourceSets["test"])
        }
    }
}

// ---------------------------------------------------------------------------------------------------------------------
//region // Test mod file generation

val generatedTest: File = file("$buildDir/generated/test")

sourceSets {
    test {
        java.srcDir(generatedTest.resolve("java"))
        resources.srcDir(generatedTest.resolve("resources"))
    }
}

val generateTestMixinConnector = tasks.register<GenerateMixinConnector>("generateTestMixinConnector") {
    from(sourceSets.test)
    outputRoot.set(generatedTest.resolve("java"))
    mixinName.set("gen.core.TestMixinConnector")
}
val generateTestCoremodsJson = tasks.register<GenerateCoremodsJson>("generateTestCoremodsJson") {
    from(sourceSets.test)
    outputRoot.set(generatedTest.resolve("resources"))
}
val generateTestModInfo = tasks.register<GenerateModInfo>("generateTestModInfo") {
    modid.set("ll-${project.name}-test")
    outputRoot.set(generatedTest.resolve("resources"))
}

tasks.named("compileTestJava") {
    dependsOn(generateTestMixinConnector)
}
tasks.named<ProcessResources>("processTestResources") {
    dependsOn(generateTestCoremodsJson)
    dependsOn(generateTestModInfo)
}

//endregion // Test mod file generation
// ---------------------------------------------------------------------------------------------------------------------

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

dependencies {
//    "publishedRuntime"(project(":zzz:librarianlib"))
}

artifacts {
    add("publishedApi", deobfJar)
    add("publishedSources", sourcesJar)
    add("publishedJavadoc", dokkaJar)
    add("publishedObf", obfJar)
}

publishing.publications.named<MavenPublication>("maven") {
    artifactId = module.name
}

//endregion // Publishing
// ---------------------------------------------------------------------------------------------------------------------
