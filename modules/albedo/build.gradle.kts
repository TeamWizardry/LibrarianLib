plugins {
    `module-conventions`
}

module {
}

dependencies {
    liblib(project(":core"))
    testApi(project(":testcore"))
}
