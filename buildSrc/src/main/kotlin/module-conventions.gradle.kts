@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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

    val liblib = create("liblib") {
        description = "Inter-module dependencies"

        canBe(consumed = false, resolved = false)
        api.get().extendsFrom(this)
        getByName("publishedApi").extendsFrom(this)
    }
}

val validateMixinApplication = tasks.register<ValidateMixinApplication>("validateMixinApplication") {
    from(sourceSets.main)
    from(sourceSets.test)
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

val mod_version: String by project

tasks.named("compileTestJava") {
    dependsOn(generateTestMixinConnector)
    dependsOn(validateMixinApplication)
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
    classifier = ""
    includeEmptyDirs = false

    from(sourceSets.main.map { it.output })
    dependsOn(tasks.named("classes"))
    dependsOn(tasks.named("processResources"))

    doLast {
        includedDependencies.forEach {
            logger.info("Shading ${it.name}")
        }
    }

    commonConfig.shadowRules {
        relocate(it.from, it.to)
    }
}

val obfJar = tasks.register<Jar>("obfJar") {
    dependsOn(deobfJar)
    classifier = "obf"
    from(deobfJar.map { zipTree(it.archiveFile) })
}
reobf.create("obfJar")

val shadowSources = tasks.register<ShadowCopy>("shadowSources") {
    relocators.set(deobfJar.get().relocators)

    from(sourceSets.main.map { it.allSource })
    sourcesFrom(configurations["shade"])
    into("$buildDir/shadowSources")
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    classifier = "sources"
    includeEmptyDirs = false
    from(shadowSources.map { it.outputs })
}

//endregion // Build configuration
// ---------------------------------------------------------------------------------------------------------------------

// ---------------------------------------------------------------------------------------------------------------------
//region // Publishing

dependencies {
    "publishedRuntime"(project(":zzz:librarianlib"))
}

artifacts {
    add("publishedApi", deobfJar)
    add("publishedSources", sourcesJar)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.teamwizardry.librarianlib"
            artifactId = module.name
            version = commonConfig.version

            from(components["mod"])
        }
    }
}

//endregion // Publishing
// ---------------------------------------------------------------------------------------------------------------------
