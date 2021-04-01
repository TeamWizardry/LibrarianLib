plugins {
    `module-conventions`
}

dependencies {
    implementation("dev.thecodewarrior.mirror:mirror:1.0.0b1") {
        exclude(group = "org.jetbrains.kotlin")
    }
}