plugins {
    `liblib-module-fabric`
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.core.fabric.CoreCommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.core.fabric.CoreClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.core.fabric.CoreServerInitializer")
    mixin("ll/core/core.mixins.json")
}
