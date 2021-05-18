plugins {
    `module-conventions`
}

module {
    displayName = "Albedo"
    description = "Making GLSL shaders simple"
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.albedo.LibLibAlbedo\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.albedo.LibLibAlbedo\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.albedo.LibLibAlbedo\$ServerInitializer")
    mixin("ll/albedo/albedo.mixins.json")
}

configureFabricTestModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.albedo.test.LibLibAlbedoTest\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.albedo.test.LibLibAlbedoTest\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.albedo.test.LibLibAlbedoTest\$ServerInitializer")
}

dependencies {
    liblib(project(":core"))
    testApi(project(":testcore"))
}
