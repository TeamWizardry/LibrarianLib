plugins {
    `module-conventions`
}

module {
    displayName = "Etcetera"
    description = "Minor utilities that don't warrant their own modules"
}
val commonConfig = rootProject.the<CommonConfigExtension>()

configureFabricModJson {
}

configureFabricTestModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.etcetera.test.LLEtceteraTestCommon")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.etcetera.test.LLEtceteraTestClient")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.etcetera.test.LLEtceteraTestServer")
}

dependencies {
    liblib(project(":core"))
    testApi(project(":testcore"))
}
