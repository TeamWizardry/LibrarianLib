plugins {
    `module-conventions`
}

module {
    displayName = "${humanName}"
    description = "${description}"
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.${lowername}.LibLib${PascalName}\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.${lowername}.LibLib${PascalName}\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.${lowername}.LibLib${PascalName}\$ServerInitializer")
    mixin("ll/${lowername}/${lowername}.mixins.json")
}

configureFabricTestModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.${lowername}.test.LibLib${PascalName}Test\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.${lowername}.test.LibLib${PascalName}Test\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.${lowername}.test.LibLib${PascalName}Test\$ServerInitializer")
}

dependencies {
    liblib(project(":core"))
    testApi(project(":testcore"))
}
