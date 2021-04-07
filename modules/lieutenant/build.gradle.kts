plugins {
    `module-conventions`
    `mixin-conventions`
}

dependencies {
    api(project(":core"))
    testApi(project(":testcore"))
}
