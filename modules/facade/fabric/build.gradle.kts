plugins {
    `liblib-module-fabric`
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.facade.fabric.FacadeCommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.facade.fabric.FacadeClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.facade.fabric.FacadeServerInitializer")
    mixin("ll/facade/facade.mixins.json")
}
