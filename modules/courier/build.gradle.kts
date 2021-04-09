plugins {
    `module-conventions`
}

dependencies {
    liblib(project(":core"))
    liblib(project(":scribe"))
    testApi(project(":testcore"))
    testApi(project(":scribe"))
}
