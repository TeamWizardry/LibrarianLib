plugins {
    `module-conventions`
}

dependencies {
    shade("dev.thecodewarrior.mirror:mirror:1.0.0b1") {
        exclude(group = "org.jetbrains.kotlin")
    }
}