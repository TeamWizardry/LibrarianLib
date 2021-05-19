plugins {
    `module-conventions`
}

module {
    displayName = "Facade"
    description = "A feature-rich, flexible GUI framework"
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.facade.LibLibFacade\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.facade.LibLibFacade\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.facade.LibLibFacade\$ServerInitializer")
    mixin("ll/facade/facade.mixins.json")
}

configureFabricTestModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.facade.test.LibLibFacadeTest\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.facade.test.LibLibFacadeTest\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.facade.test.LibLibFacadeTest\$ServerInitializer")
}

val bitfont_version: String by project

dependencies {
    liblib(project(":core"))
    liblib(project(":mosaic"))
    liblib(project(":albedo"))
    liblib(project(":etcetera"))
    liblib(project(":scribe"))
    liblib(project(":courier"))

    api("dev.thecodewarrior:bitfont:$bitfont_version")
    include("dev.thecodewarrior:bitfont:$bitfont_version")
    include("com.ibm.icu:icu4j:63.1")
    include("org.msgpack:msgpack-core:0.8.16")

    // we have to include ICU in the classpath so it overrides Mojang's stripped down copy
    devClasspath("com.ibm.icu:icu4j:63.1")

    testApi(project(":testcore"))
}

/*
kotlin.sourceSets {
    main {
        kotlin.exclude(
            "com/teamwizardry/librarianlib/facade/compat/**",
            "com/teamwizardry/librarianlib/facade/container/**",
            "com/teamwizardry/librarianlib/facade/pastry/**"
        )
    }
    test {
        kotlin.exclude(
            "com/teamwizardry/librarianlib/facade/example/**",
            "com/teamwizardry/librarianlib/facade/test/containers/**",
            "com/teamwizardry/librarianlib/facade/test/screens/pastry/**"
        )
    }
}
 */