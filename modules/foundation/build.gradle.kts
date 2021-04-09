plugins {
    `module-conventions`
    `mixin-conventions`
}

dependencies {
    liblib(project(":core"))
    liblib(project(":scribe"))
    liblib(project(":courier"))
    liblib(project(":facade"))
    testApi(project(":testcore"))
}
