plugins {
    `module-conventions`
}

module {
    displayName = "Glitter"
    description = "High-performance particle systems"
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.glitter.LibLibGlitter\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.glitter.LibLibGlitter\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.glitter.LibLibGlitter\$ServerInitializer")
    mixin("ll/glitter/glitter.mixins.json")
}

configureFabricTestModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.glitter.test.LibLibGlitterTest\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.glitter.test.LibLibGlitterTest\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.glitter.test.LibLibGlitterTest\$ServerInitializer")
}

dependencies {
    liblib(project(":core"))
    liblib(project(":etcetera"))
    liblib(project(":albedo"))
    testApi(project(":testcore"))
    includeImplementation("org.magicwerk:brownies-collections:0.9.13")
}
