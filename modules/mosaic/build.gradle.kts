plugins {
    `module-conventions`
}

module {
    displayName = "Mosaic"
    description = "Data-driven spritesheets, designed for Facade"
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.mosaic.LibLibMosaic\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.mosaic.LibLibMosaic\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.mosaic.LibLibMosaic\$ServerInitializer")
//    mixin("ll/mosaic/mosaic.mixins.json")
}

configureFabricTestModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.mosaic.test.LibLibMosaicTest\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.mosaic.test.LibLibMosaicTest\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.mosaic.test.LibLibMosaicTest\$ServerInitializer")
}

dependencies {
    liblib(project(":core"))
    liblib(project(":albedo"))
}
