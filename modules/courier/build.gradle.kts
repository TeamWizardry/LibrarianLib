plugins {
    `module-conventions`
}

dependencies {
    api(project(":core"))
    api(project(":scribe"))
    testApi(project(":testcore"))
    testApi(project(":scribe"))
}
