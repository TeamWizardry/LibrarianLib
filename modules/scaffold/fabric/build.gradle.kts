plugins {
    `liblib-module-fabric`
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.scaffold.fabric.ScaffoldCommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.scaffold.fabric.ScaffoldClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.scaffold.fabric.ScaffoldServerInitializer")
    mixin("ll/scaffold/scaffold.mixins.json")
}
