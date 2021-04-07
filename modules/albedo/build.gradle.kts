plugins {
    `module-conventions`
}

module {
}

dependencies {
    api(project(":core"))
    testApi(project(":testcore"))
}
