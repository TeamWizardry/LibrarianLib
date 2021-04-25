
plugins {
    `module-conventions`
    `mixin-conventions`
}

module {
}
val commonConfig = rootProject.the<CommonConfigExtension>()

dependencies {
    api("dev.thecodewarrior.mirror:mirror:1.0.0b1")
    include("dev.thecodewarrior.mirror:mirror:1.0.0b1")
//    testApi(project(":testcore"))
}
