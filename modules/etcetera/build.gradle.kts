plugins {
    `module-conventions`
}

module {
    displayName = "Etcetera"
    description = "Minor utilities that don't warrant their own modules"
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.etcetera.LibLibEtcetera\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.etcetera.LibLibEtcetera\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.etcetera.LibLibEtcetera\$ServerInitializer")
    mixin("ll/etcetera/etcetera.mixins.json")
}

configureFabricTestModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.etcetera.test.LibLibEtceteraTest\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.etcetera.test.LibLibEtceteraTest\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.etcetera.test.LibLibEtceteraTest\$ServerInitializer")
}

dependencies {
    liblib(project(":core"))
    testApi(project(":testcore"))
}
