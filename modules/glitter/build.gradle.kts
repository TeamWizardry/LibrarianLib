plugins {
    `module-conventions`
}

module {
    shadow("org.magicwerk.brownies")
}

dependencies {
    liblib(project(":core"))
    liblib(project(":etcetera"))
    testApi(project(":testcore"))
    shade("org.magicwerk:brownies-collections:0.9.13")
}
