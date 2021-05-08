
plugins {
    `module-conventions`
    `mixin-conventions`
}

module {
    displayName = "Core"
    description = "Core classes used by the other LibrarianLib modules"
}
val commonConfig = rootProject.the<CommonConfigExtension>()

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.LibrarianLib")
    mixin("ll/core/core.mixins.json")
}

configureFabricTestModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.core.test.LLCoreTestCommon")
}

dependencies {
    api("dev.thecodewarrior.mirror:mirror:1.0.0b1")
    include("dev.thecodewarrior.mirror:mirror:1.0.0b1")
    testApi(project(":testcore"))
}
