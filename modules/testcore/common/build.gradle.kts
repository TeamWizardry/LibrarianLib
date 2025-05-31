plugins {
    `liblib-module-common`
}

dependencies {
    modImplementation("dev.architectury:architectury:${rootProject.property("architectury_api_version")}")
}

tasks.withType<PublishToMavenRepository>().configureEach { enabled = false }
tasks.withType<PublishToMavenLocal>().configureEach { enabled = false }
