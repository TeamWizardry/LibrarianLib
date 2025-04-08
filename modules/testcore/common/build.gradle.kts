plugins {
    `liblib-module-common`
}

dependencies {
    modImplementation("dev.architectury:architectury:${rootProject.property("architectury_api_version")}")
}