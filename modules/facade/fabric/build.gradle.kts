plugins {
    `liblib-module-fabric`
}

configureFabricModJson {
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.facade.fabric.FabricFacadeClientInitializer")
    mixin("ll/facade/facade.mixins.json")
}
