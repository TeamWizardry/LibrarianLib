plugins {
    `module-conventions`
}

module {
    displayName = "Scribe"
    description = "Automatic, annotation-driven serialization"
    shadow("dev.thecodewarrior.prism")
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.scribe.LibLibScribe\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.scribe.LibLibScribe\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.scribe.LibLibScribe\$ServerInitializer")
    mixin("ll/scribe/scribe.mixins.json")
}

configureFabricTestModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.scribe.test.LibLibScribeTest\$CommonInitializer")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.scribe.test.LibLibScribeTest\$ClientInitializer")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.scribe.test.LibLibScribeTest\$ServerInitializer")
}

dependencies {
    liblib(project(":core"))
    shade("dev.thecodewarrior.prism:prism:0.1.0b1")
}
