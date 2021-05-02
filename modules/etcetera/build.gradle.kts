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

dependencies {
    liblib(project(":core"))
    testApi(project(":testcore"))
}
