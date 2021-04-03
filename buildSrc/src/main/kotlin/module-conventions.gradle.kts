@file:Suppress("PublicApiImplicitType")

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
//region // File generation
// ---------------------------------------------------------------------------------------------------------------------

val generatedMain: File = file("$buildDir/generated/main")
val generatedTest: File = file("$buildDir/generated/test")

sourceSets {
    main {
        java.srcDir(generatedMain.resolve("java"))
        resources.srcDir(generatedMain.resolve("resources"))
    }

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


tasks.named("compileTestJava") {
    dependsOn(generateTestMixinConnector)
}
tasks.named("processTestResources") {
    dependsOn(generateTestCoremodsJson)
}

// ---------------------------------------------------------------------------------------------------------------------
//endregion // File generation
// ---------------------------------------------------------------------------------------------------------------------
