plugins {
    `module-conventions`
}

module {
    shadow("dev.thecodewarrior.prism")
}

dependencies {
    liblib(project(":core"))
    testApi(project(":testcore"))
    shade("dev.thecodewarrior.prism:prism:0.1.0b1")
}
