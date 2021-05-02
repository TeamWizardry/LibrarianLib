plugins {
    `module-conventions`
}

module {
    displayName = "Mosaic"
    description = "Data-driven spritesheets, designed for Facade"
}

configureFabricModJson {
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.mosaic.LibLibMosaicClient")
}

dependencies {
    liblib(project(":core"))
    testApi(project(":testcore"))
}
