plugins {
    `module-conventions`
}

dependencies {
    api(project(":core"))
    api(project(":etcetera"))
    testApi(project(":testcore"))
    shade("org.magicwerk:brownies-collections:0.9.13")
}
