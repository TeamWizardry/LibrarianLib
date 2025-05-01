plugins {
    `liblib-module-fabric`
}

configureFabricModJson {
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.glitter.fabric.FabricGlitterClientInitializer")
    mixin("ll/glitter/glitter.mixins.json")
}
