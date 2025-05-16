plugins {
    `liblib-module-neoforge`
}

configureNeoForgeModsToml {
//    iconFile.set(rootDir.resolve("logo/test_icon.png"))
    mixin("ll/testcore/testcore.mixins.json")

    for (module in commonConfig.modules) {
        mod {
            modId = "${module.modid}_test"
            version = commonConfig.version
            displayName = "${module.name} Tests"
            description = "Tests for ${module.name}"
        }
    }
}

dependencies {
    modImplementation("dev.architectury:architectury-neoforge:${rootProject.property("architectury_api_version")}")

    commonConfig.modules.forEach { module ->
        shadowBundle(project(path = module.testModPath, configuration = "transformProductionNeoForge"))
    }
}
