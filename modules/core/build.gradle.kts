
plugins {
    `module-conventions`
}

module {
    displayName = "Core"
    description = "Core classes used by the other LibrarianLib modules"
    shadow("dev.thecodewarrior.mirror")
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.LibrarianLib")
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.core.LibLibCore\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.core.LibLibCore\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.core.LibLibCore\$ServerInitializer")
    mixin("ll/core/core.mixins.json")
}

configureFabricTestModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.core.test.LibLibCoreTest\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.core.test.LibLibCoreTest\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.core.test.LibLibCoreTest\$ServerInitializer")
}

dependencies {
    shade("dev.thecodewarrior:mirror:1.0.0-beta.4")
}
