@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java-library")
    id("kotlin-conventions")
    id("minecraft-conventions")
    id("com.github.johnrengelman.shadow")
}

apply<LibLibModulePlugin>()
val commonConfig = rootProject.the<CommonConfigExtension>()

sourceSets {
    main {
        resources.srcDir("src/main/datagen")
    }
    test {
        resources.srcDir("src/test/datagen")
    }
}

configurations {
    create("shade") {
        compileOnly.get().extendsFrom(this)
        testCompileOnly.get().extendsFrom(this)
        api.get().extendsFrom(this)
    }
    val mod = create("mod")
    create("clientMod").extendsFrom(mod)
    create("serverMod").extendsFrom(mod)
    create("dataMod").extendsFrom(mod)
    create("devClasspath")
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
    if(name == "reobfJar") {
        enabled = false
    }
}

val deobfJar = tasks.register<ShadowJar>("deobfJar") {
    configurations = listOf(project.configurations.getByName("shade"))
    classifier = "deobf"
    includeEmptyDirs = false

    from(sourceSets.main.map { it.output })
    dependsOn(tasks.named("classes"))
    dependsOn(tasks.named("processResources"))

    doFirst {
    }

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

//endregion // Build configuration
// ---------------------------------------------------------------------------------------------------------------------
