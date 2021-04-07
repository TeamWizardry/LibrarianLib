plugins {
    `module-conventions`
    `mixin-conventions`
}

module {
    shadow("dev.thecodewarrior.mirror")
}

dependencies {
    shade("dev.thecodewarrior.mirror:mirror:1.0.0b1") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testApi(project(":testcore"))
}