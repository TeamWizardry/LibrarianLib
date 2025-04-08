plugins {
    `liblib-module-fabric`
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.testcore.fabric.TestCoreCommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.testcore.fabric.TestCoreClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.testcore.fabric.TestCoreServerInitializer")
    mixin("ll/testcore/testcore.mixins.json")
}

dependencies {
    modImplementation("dev.architectury:architectury-fabric:${rootProject.property("architectury_api_version")}")
}