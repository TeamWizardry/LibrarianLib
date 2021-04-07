plugins {
    `module-conventions`
}

module {
    shadow("dev.thecodewarrior.prism")
}

dependencies {
    api(project(":core"))
    testApi(project(":testcore"))
    shade("dev.thecodewarrior.prism:prism:0.1.0b1") {
        exclude(module = "mirror")
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.apache.logging.log4j")
    }
}
