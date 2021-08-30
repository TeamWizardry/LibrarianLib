plugins {
    `module-conventions`
}

module {
    displayName = "Courier"
    description = "Networking made easy(er)"
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.courier.LibLibCourier\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.courier.LibLibCourier\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.courier.LibLibCourier\$ServerInitializer")
    mixin("ll/courier/courier.mixins.json")
}

configureFabricTestModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.courier.test.LibLibCourierTest\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.courier.test.LibLibCourierTest\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.courier.test.LibLibCourierTest\$ServerInitializer")
}

dependencies {
    liblib(project(":core"))
    liblib(project(":scribe"))
}
