plugins {
    `module-conventions`
}

dependencies {
    api(project(":core"))
    api(project(":scribe"))
    api(project(":courier"))
    api(project(":facade"))
    testApi(project(":testcore"))
}
