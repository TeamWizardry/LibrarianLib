@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java-library")
    id("kotlin-conventions")
    id("minecraft-conventions")
    id("org.spongepowered.mixin")
}

apply<LibLibModulePlugin>()


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

// ---------------------------------------------------------------------------------------------------------------------
//region // Test mod file generation
// ---------------------------------------------------------------------------------------------------------------------

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
}
tasks.named<ProcessResources>("processTestResources") {
    dependsOn(generateTestCoremodsJson)
    dependsOn(generateTestModInfo)
    filesMatching("**/mods.toml") {
        filter(ReplaceTokens::class, "tokens" to mapOf("version" to mod_version))
    }
}

// ---------------------------------------------------------------------------------------------------------------------
//endregion // Test mod file generation
// ---------------------------------------------------------------------------------------------------------------------
