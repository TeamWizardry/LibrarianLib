plugins {
    `module-conventions`
    `mixin-conventions`
}

dependencies {
    liblib(project(":core"))
    testApi(project(":testcore"))
}
