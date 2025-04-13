plugins {
    `liblib-module-fabric`
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.testcore.fabric.FabricTestCoreCommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.testcore.fabric.FabricTestCoreClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.testcore.fabric.FabricTestCoreServerInitializer")
    iconFile.set(rootDir.resolve("logo/test_icon.png"))
    mixin("ll/testcore/testcore.mixins.json")
}

dependencies {
    modImplementation("dev.architectury:architectury-fabric:${rootProject.property("architectury_api_version")}")
}